package io.catalyte.training.movierentals.domains.movie;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.movierentals.constants.LoggingConstants;
import io.catalyte.training.movierentals.constants.StringConstants;
import io.catalyte.training.movierentals.exceptions.BadRequest;
import io.catalyte.training.movierentals.exceptions.RequestConflict;
import io.catalyte.training.movierentals.exceptions.ResourceNotFound;
import io.catalyte.training.movierentals.exceptions.ServiceUnavailable;
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
import org.springframework.stereotype.Service;

/**
 * This class provides the implementation for the ProductService interface.
 */
@Service
public class EncounterServiceImpl implements EncounterService {

  private final Logger logger = LogManager.getLogger(EncounterServiceImpl.class);

  EncounterRepository encounterRepository;

  @Autowired
  public EncounterServiceImpl(EncounterRepository encounterRepository) {
    this.encounterRepository = encounterRepository;
  }

  /**
   * Retrieves all products from the database, optionally making use of an example if it is passed.
   *
   * @return - a list of products matching the example, or all products if no example was passed
   */
  public List<Encounter> getEncounters() {
    try {
      return encounterRepository.findAll();
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }

  /**
   * Retrieves the product with the provided id from the database.
   *
   * @param id - the id of the product to retrieve
   * @return - the product
   */
  public Encounter getEncounterById(Long id) {
    Encounter encounter;

    try {
      encounter = encounterRepository.findById(id).orElse(null);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }

    if (encounter != null) {
      return encounter;
    } else {
      logger.info(LoggingConstants.GET_BY_ID_FAILURE(id));
      throw new ResourceNotFound(LoggingConstants.GET_BY_ID_FAILURE(id));
    }
  }

  /**
   * Adds a movie to the database
   *
   * @param encounter - product object
   * @return list of movie objects that are added to database
   */
  public Encounter saveEncounter(Encounter encounter) {
//    List<String> encounterErrors = getMovieErrors(encounter);
//    if (!movieErrors.isEmpty()) {
//      throw new BadRequest(String.join("\n", movieErrors));
//    }

    try {
      return encounterRepository.save(encounter);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }

  /**
   * Updates movie in the database.
   * @param id - id of movie to be updated
   * @param encounter - updated movie payload
   * @return - updated movie object
   */

  //TODO: Update Logging constants for Resource not found, etc.
  public Encounter updateMovie(Long id, Encounter encounter){
    Encounter findEncounter;

    try {
     findEncounter  = encounterRepository.findById(id).orElse(null);
    }catch(DataAccessException e) {
      logger.error(e.getMessage());
      throw new ResourceNotFound(LoggingConstants.UPDATE_MOVIE_FAILURE);
    }

  if(findEncounter != null) {
//    findMovie.setSku(movie.getSku());
//    findMovie.setGenre(movie.getGenre());
//    findMovie.setDirector(movie.getDirector());
//    findMovie.setTitle(movie.getTitle());
//    findMovie.setDailyRentalCost(movie.getDailyRentalCost());
//    findMovie.setId(id);
  }
    List<String> movieErrors = getMovieErrors(encounter);

    if (!movieErrors.isEmpty()) {
      throw new BadRequest(String.join("\n", movieErrors));
    }

    try{
      return encounterRepository.save(findEncounter);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }

  /**
   * Deletes movie in the database.
   * @param id - id of the movie to be deleted
   */
  public void deleteMovie(Long id){
    Encounter findEncounter;

    try {
      findEncounter  = encounterRepository.findById(id).orElse(null);
    }catch(DataAccessException e) {
      logger.error(e.getMessage());
      throw new ResourceNotFound(LoggingConstants.UPDATE_MOVIE_FAILURE);
    }

    //validation for if there are encounters already?

    if(findEncounter != null){
      try {
        encounterRepository.deleteById(id);
      } catch (DataAccessException e){
        logger.error(e.getMessage());
        throw new ServiceUnavailable(e.getMessage());
      }
    }

  }


  /**
   * Helper method that reads a movie and validates its properties
   *
   * @param movie movie to be validated
   * @return a list of errors
   */
  public List<String> getMovieErrors(Encounter movie) {
    List<String> errors = new ArrayList<>();
    Boolean dailyRentalCostIsNotValid = validateDailyRentalCost(movie);
    List<String> emptyFields = getFieldsEmptyOrNull(movie).get("emptyFields");
    List<String> nullFields = getFieldsEmptyOrNull(movie).get("nullFields");
//    Boolean skuFormatIsValid= validateMovieSkuFormat(movie);

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
  public Boolean validateDailyRentalCost(Encounter movie) {
//    if (movie.getDailyRentalCost() != null) {
//      //Split price by the decimal
//      String[] rentalCostString = String.valueOf(movie.getDailyRentalCost()).split("\\.");
//      Boolean priceMoreThan2Decimals = rentalCostString[1].length() > 2;
//      Boolean priceLessThanZero = movie.getDailyRentalCost() < 0;
//      return priceLessThanZero || priceMoreThan2Decimals;
//    }
    return false;
  }

  /**
   * Validates the format of a movie SKU to match 'XXXX-DDDD' where X is a capital letter
   * and D is a digit.
   *
   * @param movie product to be validated
   * @return boolean if product has valid quantity
   */
//  public Boolean validateMovieSkuFormat(Encounter movie) {
//    String regex = "^[A-Z]{6}-\\d{4}$";
//    Pattern pattern = Pattern.compile(regex);
////    if (movie.getSku() != null) {
////      Matcher matcher = pattern.matcher(movie.getSku());
////      return matcher.matches();
////    }
////    return false;
//  };

  /**
   * Checks whether the sku of a movie attempting to be added or updated already exists in the database.
   * @param newMovie - movie to be saved
   * @return Boolean if the sku exists already.
   */
//  public Boolean movieSkuExists(Encounter newMovie){
//    List<Encounter> allMovies = movieRepository.findAll();
//    if(newMovie.getSku() != null){
//      for(Encounter movie : allMovies){
//        if (movie.getSku().equals(newMovie.getSku()) && movie.getId() != newMovie.getId()) {
//        return true;
//          }
//      }
//    }
//    return false;
//  }

  /**
   * Reads a movie fields and checks for fields that are empty or null
   *
   * @param encounter movie to be validated
   * @return A Hashmap {"emptyFields": List of empty fields, "nullFields": list of null fields}
   */
  public HashMap<String, List<String>> getFieldsEmptyOrNull(Encounter encounter) {
    List<Field> encounterFields = Arrays.asList(Encounter.class.getDeclaredFields());
    List<String> encounterFieldNames = new ArrayList<>();
    List<String> emptyFields = new ArrayList<>();
    List<String> nullFields = new ArrayList<>();
    HashMap<String, List<String>> results = new HashMap<>();
    //Get product field names
    encounterFields.forEach((field -> encounterFieldNames.add(field.getName())));
    //Remove id as product will not have an id before it is saved
    encounterFieldNames.remove("id");
    //Convert product to a HashMap
    ObjectMapper mapper = new ObjectMapper();
    Map encounterMap = mapper.convertValue(encounter, HashMap.class);
    //Loop through each fieldName to retrieve each product mapping value of the field
    encounterFieldNames.forEach((field) -> {
      //Check if the value for the product's field is null or empty and place in the corresponding list
      if (encounterMap.get(field) == null) {
        nullFields.add(field);
      } else if (encounterMap.get(field).toString().trim() == "") {
        emptyFields.add(field);
      }
    });
    //place each list in the results
    results.put("emptyFields", emptyFields);
    results.put("nullFields", nullFields);
    return results;
  }


}