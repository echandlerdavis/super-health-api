package io.catalyte.training.movierentals.domains.rental;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.movierentals.constants.StringConstants;
import io.catalyte.training.movierentals.domains.movie.Movie;
import io.catalyte.training.movierentals.domains.movie.MovieRepository;
import io.catalyte.training.movierentals.domains.movie.MovieService;
import io.catalyte.training.movierentals.domains.rental.Rental;
import io.catalyte.training.movierentals.domains.rental.RentalRepository;
import io.catalyte.training.movierentals.domains.rental.RentalService;
import io.catalyte.training.movierentals.domains.rental.RentedMovie;
import io.catalyte.training.movierentals.domains.rental.RentedMovieRepository;
import io.catalyte.training.movierentals.exceptions.BadRequest;
import io.catalyte.training.movierentals.exceptions.MultipleUnprocessableContent;
import io.catalyte.training.movierentals.exceptions.ResourceNotFound;
import io.catalyte.training.movierentals.exceptions.ServerError;
import io.catalyte.training.movierentals.exceptions.UnprocessableContent;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class RentalServiceImpl implements RentalService {

  private final Logger logger = LogManager.getLogger(RentalServiceImpl.class);

  RentalRepository rentalRepository;
  RentedMovieRepository rentedMovieRepository;
  MovieRepository movieRepository;

  @Autowired
  public RentalServiceImpl(RentalRepository rentalRepository,
      RentedMovieRepository rentedMovieRepository, MovieRepository movieRepository) {
    this.rentalRepository = rentalRepository;
    this.rentedMovieRepository = rentedMovieRepository;
    this.movieRepository = movieRepository;
  }

  /**
   * Retrieves all purchases from the database
   *
   * @return
   */
  public List<Rental> getRentals() {
    try {
      return rentalRepository.findAll();
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  /**
   * Search for single rental by rental id.
   *
   * @param id Long id
   * @return a single rental object.
   */
  public Rental getRentalById(Long id) {
    Rental rental;

    try{
      rental = rentalRepository.findById(id).orElse(null);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }

    if(rental !=null){
      return rental;
    } else {
      logger.info("Get by id failed, it does not exist in the database: " + id);
      throw new ResourceNotFound("Get by id failed, it does not exist in the database: " + id);
    }
  }

  /**
   * Persists a rental to the database
   *
   * @param newRental - the rental to persist
   * @return the persisted rental object
   */
  public Rental saveRental(Rental newRental) {
    List<String> rentalErrors = getRentalErrors(newRental);

    if(!rentalErrors.isEmpty()){
      throw new BadRequest(String.join("\n", rentalErrors));
    }

    Rental savedRental;

    try {
      savedRental = rentalRepository.save(newRental);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }

    newRental.setId(savedRental.getId());
    handleRentedMovies(newRental);
    savedRental.setRentedMovies(rentedMovieRepository.findByRental(newRental));

    return savedRental;
  }

  private void handleRentedMovies(Rental rental){
    Set<RentedMovie> rentedMovieSet = rental.getRentedMovies();
    Set<RentedMovie> findRentedMovies = rentedMovieRepository.findByRental(rental);

    if(findRentedMovies != null){
      rentedMovieRepository.deleteAll(findRentedMovies);
    }

    if(rentedMovieSet != null){
      rentedMovieSet.forEach(rentedMovie -> {

        rentedMovie.setRental(rental);
        rentedMovie.setId(null);

        try {
          rentedMovieRepository.save(rentedMovie);
        } catch (DataAccessException e) {
          logger.error(e.getMessage());
          throw new ServerError(e.getMessage());
        }
      });
    }
  }

  public Rental updateRental(Long id, Rental updatedRental){
    Rental findRental = rentalRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFound("Cannot update a rental that does not exist."));

    List<String> rentalErrors = getRentalErrors(updatedRental);

    if(!rentalErrors.isEmpty()){
      throw new BadRequest(String.join("\n", rentalErrors));
    }

    Rental savedRental;
    findRental.setId(id);
    findRental.setRentalDate(updatedRental.getRentalDate());
    findRental.setRentedMovies(null);
    findRental.setRentedMovies(updatedRental.getRentedMovies());
    handleRentedMovies(findRental);
    findRental.setRentalTotalCost(updatedRental.getRentalTotalCost());
    try {
      savedRental = rentalRepository.save(findRental);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
    return savedRental;
  }

  public void deleteRentalById(Long id){
    if(rentalRepository.findById(id) == null){
      throw new ResourceNotFound("You cannot delete a rental that doesn't exist.");
    }

    try {
      rentalRepository.deleteById(id);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  /**
   * Helper method that reads a rental and validates its properties
   *
   * @param rental rental to be validated
   * @return a list of errors
   */
  public List<String> getRentalErrors(Rental rental) {
    List<String> errors = new ArrayList<>();
    Boolean dailyRentalCostIsNotValid = validateTotalRentalCost(rental);
    List<String> emptyFields = getRentalFieldsEmptyOrNull(rental).get("emptyFields");
    List<String> nullFields = getRentalFieldsEmptyOrNull(rental).get("nullFields");
    Set<String> rentedMovieErrors = getRentedMovieErrors(rental);

    if (!nullFields.isEmpty()) {
      errors.add(StringConstants.MOVIE_FIELDS_NULL(nullFields));
    }

    if (!emptyFields.isEmpty()) {
      errors.add(StringConstants.MOVIE_FIELDS_EMPTY(emptyFields));
    }

    if (dailyRentalCostIsNotValid) {
      errors.add(StringConstants.MOVIE_RENTAL_COST_INVALID);
    }

    if(!rentedMovieErrors.isEmpty()){
      errors.addAll(rentedMovieErrors);
    }

    return errors;
  }

  /**
   * Checks that total rental cost is a double value greater than zero and does not have more than 2 digits after the
   * decimal
   * <p>
   * Because price is stored as a double, regardless of input the product will always have 1 digit
   * after the decimal even if input as an integer, or with 2 zeros after decimal
   *
   * @param rental movie to be validated
   * @return boolean if dailyRentalCost is valid
   */
  public Boolean validateTotalRentalCost(Rental rental) {
    if (rental.getRentalTotalCost() != null) {
      //Split price by the decimal
      String[] rentalCostString = String.valueOf(rental.getRentalTotalCost()).split("\\.");
      Boolean priceMoreThan2Decimals = rentalCostString[1].length() > 2;
      Boolean priceLessThanZero = rental.getRentalTotalCost() < 0;
      return priceLessThanZero || priceMoreThan2Decimals;
    }
    return false;
  }

  /**
   * Reads a rental fields and checks for fields that are empty or null
   *
   * @param rental movie to be validated
   * @return A Hashmap {"emptyFields": List of empty fields, "nullFields": list of null fields}
   */
  public HashMap<String, List<String>> getRentalFieldsEmptyOrNull(Rental rental) {
    List<Field> rentalFields = Arrays.asList(Rental.class.getDeclaredFields());
    List<String> rentalFieldNames = new ArrayList<>();
    List<String> emptyFields = new ArrayList<>();
    List<String> nullFields = new ArrayList<>();
    HashMap<String, List<String>> results = new HashMap<>();
    //Get product field names
    rentalFields.forEach((field -> rentalFieldNames.add(field.getName())));
    //Remove id as product will not have an id before it is saved
    rentalFieldNames.remove("id");
    //Convert rental to a HashMap
    ObjectMapper mapper = new ObjectMapper();
    Map rentalMap = mapper.convertValue(rental, HashMap.class);
    //Loop through each fieldName to retrieve each rental mapping value of the field
    rentalFieldNames.forEach((field) -> {
      //Check if the value for the rental's field is null or empty and place in the corresponding list
      if (rentalMap.get(field) == null) {
        nullFields.add(field);
      } else if (rentalMap.get(field).toString().trim() == "") {
        emptyFields.add(field);
      }
    });

    //place each list in the results
    results.put("emptyFields", emptyFields);
    results.put("nullFields", nullFields);
    return results;
  }


  //Validation examples below (likely too complicated)
  private Set<String> getRentedMovieErrors(Rental rental) {
    // Get rentedMovies from each Rental
    Set<RentedMovie> rentedMovieSet = rental.getRentedMovies();
    Set<String> rentedMovieErrors = new HashSet<>();

    // If no rentedMovies add to error list
    if (rentedMovieSet == null || rentedMovieSet.size() == 0) {
      rentedMovieErrors.add(StringConstants.RENTAL_HAS_NO_RENTED_MOVIE);
    }

    //Goes through each and adds invalid movie ids to error list.
    List<Long> invalidMovieIds = new ArrayList<>();
    Set<String> emptyFields = new HashSet<>();
    Set<String> nullFields = new HashSet<>();
    rentedMovieSet.forEach(rentedMovie -> {
      if(!validateMovieIdExists(rentedMovie)){
        invalidMovieIds.add(rentedMovie.getMovieId());
      }
      if(rentedMovie.getMovieId() == null){
        nullFields.add("movieId");
      }else if(rentedMovie.getMovieId().toString().trim() == ""){
        emptyFields.add("movieId");
      }
      if(rentedMovie.getDaysRented() <= 0){
        rentedMovieErrors.add(StringConstants.RENTED_MOVIE_DAYS_RENTED_INVALID);
      }


    });

    if(!invalidMovieIds.isEmpty()){
      rentedMovieErrors.add(StringConstants.RENTED_MOVIEID_INVALID(invalidMovieIds));
    }

    if(!emptyFields.isEmpty()){
      rentedMovieErrors.add(StringConstants.RENTED_MOVIE_FIELDS_EMPTY(emptyFields));
    }

    if(!nullFields.isEmpty()){
      rentedMovieErrors.add(StringConstants.RENTED_MOVIE_FIELDS_NULL(nullFields));
    }

    return rentedMovieErrors;
    }

    private Boolean validateMovieIdExists(RentedMovie rentedMovie){
      List<Movie> allMovies = movieRepository.findAll();
      for(Movie movie : allMovies){
        if(movie.getId() == rentedMovie.getMovieId() || rentedMovie.getId() == null){
          return true;
        }
      }
      return false;
    }

  }





