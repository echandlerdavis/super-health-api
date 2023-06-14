package io.catalyte.training.movierentals.domains.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.movierentals.constants.LoggingConstants;
import io.catalyte.training.movierentals.constants.StringConstants;
import io.catalyte.training.movierentals.exceptions.BadRequest;
import io.catalyte.training.movierentals.exceptions.RequestConflict;
import io.catalyte.training.movierentals.exceptions.ResourceNotFound;
import io.catalyte.training.movierentals.exceptions.ServerError;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * This class provides the implementation for the ProductService interface.
 */
@Service
public class MovieServiceImpl implements MovieService {

  private final Logger logger = LogManager.getLogger(MovieServiceImpl.class);

  MovieRepository movieRepository;

  @Autowired
  public MovieServiceImpl(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  /**
   * Retrieves all products from the database, optionally making use of an example if it is passed.
   *
   * @param movie - an example product to use for querying
   * @return - a list of products matching the example, or all products if no example was passed
   */
  public List<Movie> getMovies(Movie movie) {
    try {
      logger.info(LoggingConstants.GET_MOVIES);
      return movieRepository.findAll(Example.of(movie));
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  /**
   * Retrieves the product with the provided id from the database.
   *
   * @param id - the id of the product to retrieve
   * @return - the product
   */
  public Movie getMovieById(Long id) {
    Movie movie;

    try {
      logger.info(LoggingConstants.GET_MOVIE_BY_ID(id));
      movie = movieRepository.findById(id).orElse(null);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }

    if (movie != null) {
      return movie;
    } else {
      logger.info(LoggingConstants.GET_BY_ID_FAILURE(id));
      throw new ResourceNotFound(LoggingConstants.GET_BY_ID_FAILURE(id));
    }
  }

  /**
   * Adds a movie to the database
   *
   * @param movie - product object
   * @return list of movie objects that are added to database
   */
  public Movie saveMovie(Movie movie) {
    List<String> movieErrors = getMovieErrors(movie);
    Boolean skuExists = movieSkuExists(movie);
    if (!movieErrors.isEmpty()) {
      throw new BadRequest(String.join("\n", movieErrors));
    }
    if(skuExists){
      throw new RequestConflict(StringConstants.MOVIE_SKU_ALREADY_EXISTS);
    }
    try {
      logger.info(LoggingConstants.POST_MOVIE);
      return movieRepository.save(movie);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  public Movie updateMovie(Long id, Movie movie){
    Movie findMovie = movieRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFound(LoggingConstants.UPDATE_MOVIE_FAILURE));

    List<String> movieErrors = getMovieErrors(movie);
    Boolean skuExists = movieSkuExists(findMovie);

    if (!movieErrors.isEmpty()) {
      throw new BadRequest(String.join("\n", movieErrors));
    }

    if(skuExists){
      throw new RequestConflict(StringConstants.MOVIE_SKU_ALREADY_EXISTS);
    }

    try{
      findMovie.setSku(movie.getSku());
      findMovie.setGenre(movie.getGenre());
      findMovie.setDirector(movie.getDirector());
      findMovie.setTitle(movie.getTitle());
      findMovie.setDailyRentalCost(movie.getDailyRentalCost());
      findMovie.setId(id);
      logger.info(LoggingConstants.UPDATE_MOVIE(id));
      return movieRepository.save(findMovie);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  public void deleteMovie(Long id){
    if(movieRepository.findById(id) == null){
      throw new ResourceNotFound(LoggingConstants.DELETE_MOVIE_FAILURE);
    }

    try {
      logger.info(LoggingConstants.DELETE_MOVIE(id));
      movieRepository.deleteById(id);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }

  }


  /**
   * Helper method that reads a movie and validates it's properties
   *
   * @param movie movie to be validated
   * @return a list of errors
   */
  public List<String> getMovieErrors(Movie movie) {
    List<String> errors = new ArrayList<>();
    Boolean dailyRentalCostIsNotValid = validateDailyRentalCost(movie);
    List<String> emptyFields = getFieldsEmptyOrNull(movie).get("emptyFields");
    List<String> nullFields = getFieldsEmptyOrNull(movie).get("nullFields");
    Boolean skuFormatIsValid= validateMovieSkuFormat(movie);

    if (!nullFields.isEmpty()) {
      errors.add(StringConstants.MOVIE_FIELDS_NULL(nullFields));
    }

    if (!emptyFields.isEmpty()) {
      errors.add(StringConstants.MOVIE_FIELDS_EMPTY(emptyFields));
    }

    if (dailyRentalCostIsNotValid) {
      errors.add(StringConstants.MOVIE_RENTAL_COST_INVALID);
    }

    if (!skuFormatIsValid) {
      errors.add(StringConstants.MOVIE_SKU_INVALID);
    }

    return errors;
  }

  /**
   * Checks that daily rental cost is a double value greater than zero and does not have more than 2 digits after the
   * decimal
   * <p>
   * Because price is stored as a double, regardless of input the product will always have 1 digit
   * after the decimal even if input as an integer, or with 2 zeros after decimal
   *
   * @param movie movie to be validated
   * @return boolean if dailyRentalCost is valid
   */
  public Boolean validateDailyRentalCost(Movie movie) {
    if (movie.getDailyRentalCost() != null) {
      //Split price by the decimal
      String[] rentalCostString = String.valueOf(movie.getDailyRentalCost()).split("\\.");
      Boolean priceMoreThan2Decimals = rentalCostString[1].length() > 2;
      Boolean priceLessThanZero = movie.getDailyRentalCost() < 0;
      return priceLessThanZero || priceMoreThan2Decimals;
    }
    return false;
  }

  /**
   * Validates the format of a movie SKU to match "
   *
   * @param movie product to be validated
   * @return boolean if product has valid quantity
   */
  public Boolean validateMovieSkuFormat(Movie movie) {
    String regex = "^[A-Z]{6}-\\d{4}$";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(movie.getSku());
    if (movie.getSku() != null) {
      return matcher.matches();
    }
    return false;
  }

  public Boolean movieSkuExists(Movie newMovie){
    List<Movie> allMovies = movieRepository.findAll();
    if(newMovie.getSku() != null){
      for(Movie movie : allMovies){
          if (movie.getSku().equals(newMovie.getSku()) && movie.getId() != newMovie.getId()) {
            return true;
          }
      }
    }
    return false;
  }

  /**
   * Reads a movie fields and checks for fields that are empty or null
   *
   * @param movie movie to be validated
   * @return A Hashmap {"emptyFields": List of empty fields, "nullFields": list of null fields}
   */
  public HashMap<String, List<String>> getFieldsEmptyOrNull(Movie movie) {
    List<Field> movieFields = Arrays.asList(Movie.class.getDeclaredFields());
    List<String> movieFieldNames = new ArrayList<>();
    List<String> emptyFields = new ArrayList<>();
    List<String> nullFields = new ArrayList<>();
    HashMap<String, List<String>> results = new HashMap<>();
    //Get product field names
    movieFields.forEach((field -> movieFieldNames.add(field.getName())));
    //Remove id as product will not have an id before it is saved
    movieFieldNames.remove("id");
    //Convert product to a HashMap
    ObjectMapper mapper = new ObjectMapper();
    Map movieMap = mapper.convertValue(movie, HashMap.class);
    //Loop through each fieldName to retrieve each product mapping value of the field
    movieFieldNames.forEach((field) -> {
      //Check if the value for the product's field is null or empty and place in the corresponding list
      if (movieMap.get(field) == null) {
        nullFields.add(field);
      } else if (movieMap.get(field).toString().trim() == "") {
        emptyFields.add(field);
      }
    });
    //place each list in the results
    results.put("emptyFields", emptyFields);
    results.put("nullFields", nullFields);
    return results;
  }


}