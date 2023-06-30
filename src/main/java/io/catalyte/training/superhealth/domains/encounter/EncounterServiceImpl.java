package io.catalyte.training.superhealth.domains.encounter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.superhealth.constants.LoggingConstants;
import io.catalyte.training.superhealth.constants.StringConstants;
import io.catalyte.training.superhealth.domains.patient.Patient;
import io.catalyte.training.superhealth.domains.patient.PatientService;
import io.catalyte.training.superhealth.exceptions.BadRequest;
import io.catalyte.training.superhealth.exceptions.RequestConflict;
import io.catalyte.training.superhealth.exceptions.ResourceNotFound;
import io.catalyte.training.superhealth.exceptions.ServiceUnavailable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  PatientService patientService;

  @Autowired
  public EncounterServiceImpl(EncounterRepository encounterRepository, PatientService patientService) {
    this.encounterRepository = encounterRepository;
    this.patientService = patientService;
  }

  /**
   * Retrieves the product with the provided id from the database.
   *
   * @param id - the id of the product to retrieve
   * @return - the product
   */
  public Encounter getEncounterById(Long patientId, Long id) {
    Encounter encounter;

    try {
      encounter = encounterRepository.findById(id).orElse(null);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }

    //TODO: create constants message for this bad request;
    if(encounter.getPatient().getId() != patientId){
      throw new BadRequest();
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
//   */
  public Encounter saveEncounter(Long patientId, Encounter encounter) {
    if(encounter.getPatientId() == null){
      encounter.setPatientId(patientId);
    }
    if(patientId != encounter.getPatientId()){
      throw new RequestConflict(StringConstants.PATIENT_ID_INVALID);
    }
    encounter.setPatient(patientService.getPatientById(patientId));

    List<String> encounterErrors = getEncounterErrors(encounter);
    if (!encounterErrors.isEmpty()) {
      throw new BadRequest(String.join("\n", encounterErrors));
    }



//      if (validateTotalRentalCost(rental)) {
//      errors.add(StringConstants.RENTAL_TOTAL_COST_INVALID);
//    }
//      if(!validateDateFormat(rental) &&
//      !emptyFields.contains("rentalDate") &&
//      !nullFields.contains("rentalDate")){
//    errors.add(StringConstants.RENTAL_DATE_STRING_INVALID);
//  }

    try {
      return encounterRepository.save(encounter);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }


  /**
   * Checks that total rental cost is a double value greater than zero and does not have more than 2 digits after the
   * decimal
   * <p>
   * Because price is stored as a double, regardless of input the product will always have 1 digit
   * after the decimal even if input as an integer, or with 2 zeros after decimal
   *
   * @param patient movie to be validated
   * @return boolean if dailyRentalCost is valid
   */
//  public Boolean validateTotalRentalCost(Patient rental) {
//    if (rental.getRentalTotalCost() != null) {
//      //Split price by the decimal
//      String[] rentalCostString = String.valueOf(rental.getRentalTotalCost()).split("\\.");
//      Boolean priceMoreThan2Decimals = rentalCostString[1].length() > 2;
//      Boolean priceLessThanZero = rental.getRentalTotalCost() < 0;
//      return priceLessThanZero || priceMoreThan2Decimals;
//    }
//    return false;
//  }

//  public Boolean validateDateFormat(Patient patient) {
//    String regex = "^\\d{4}-\\d{2}-\\d{2}$";
//    Pattern pattern = Pattern.compile(regex);
//    if (patient.getD() != null) {
//      Matcher matcher = pattern.matcher(rental.getRentalDate());
//      return matcher.matches();
//    }
//    return false;
//  }

  /**
   * Updates movie in the database.
   * @param id - id of movie to be updated
   * @param encounter - updated movie payload
   * @return - updated movie object
   */

//  //TODO: Update Logging constants for Resource not found, etc.
//  public Encounter updateEncounter(Long id, Encounter encounter){
//    Encounter findEncounter;
//
//    try {
//     findEncounter  = encounterRepository.findById(id).orElse(null);
//    }catch(DataAccessException e) {
//      logger.error(e.getMessage());
//      throw new ResourceNotFound(LoggingConstants.UPDATE_ENCOUNTER_FAILURE);
//    }
//
//  if(findEncounter != null) {
////    findMovie.setSku(movie.getSku());
////    findMovie.setGenre(movie.getGenre());
////    findMovie.setDirector(movie.getDirector());
////    findMovie.setTitle(movie.getTitle());
////    findMovie.setDailyRentalCost(movie.getDailyRentalCost());
////    findMovie.setId(id);
//  }
//    List<String> movieErrors = getMovieErrors(encounter);
//
//    if (!movieErrors.isEmpty()) {
//      throw new BadRequest(String.join("\n", movieErrors));
//    }
//
//    try{
//      return encounterRepository.save(findEncounter);
//    }catch (DataAccessException e){
//      logger.error(e.getMessage());
//      throw new ServiceUnavailable(e.getMessage());
//    }
//  }
//
//  /**
//   * Deletes movie in the database.
//   * @param id - id of the movie to be deleted
//   */
//  public void deleteEncounter(Long id){
//    Encounter findEncounter;
//
//    try {
//      findEncounter  = encounterRepository.findById(id).orElse(null);
//    }catch(DataAccessException e) {
//      logger.error(e.getMessage());
//      throw new ResourceNotFound(LoggingConstants.UPDATE_ENCOUNTER_FAILURE);
//    }
//
//    //validation for if there are encounters already?
//
//    if(findEncounter != null){
//      try {
//        encounterRepository.deleteById(id);
//      } catch (DataAccessException e){
//        logger.error(e.getMessage());
//        throw new ServiceUnavailable(e.getMessage());
//      }
//    }
//
//  }


  /**
   * Helper method that reads a movie and validates its properties
   *
   * @param movie movie to be validated
   * @return a list of errors
   */
  public List<String> getEncounterErrors(Encounter movie) {
    List<String> errors = new ArrayList<>();
    Boolean dailyRentalCostIsNotValid = validateDailyRentalCost(movie);
    List<String> emptyFields = getFieldsEmptyOrNull(movie).get("emptyFields");
    List<String> nullFields = getFieldsEmptyOrNull(movie).get("nullFields");
//    Boolean skuFormatIsValid= validateMovieSkuFormat(movie);

    if (!nullFields.isEmpty()) {
      errors.add(StringConstants.FIELDS_NULL(nullFields));
    }

    if (!emptyFields.isEmpty()) {
      errors.add(StringConstants.FIELDS_EMPTY(emptyFields));
    }

    if (dailyRentalCostIsNotValid) {
      errors.add(StringConstants.STATE_INVALID);
    }

//    if (!skuFormatIsValid) {
//      errors.add(StringConstants.MOVIE_SKU_INVALID);
//    }

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