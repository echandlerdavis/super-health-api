package io.catalyte.training.movierentals.constants;

/**
 * Class for logging statements
 */
public class LoggingConstants {
  //Movie endpoints logging constants
  public static final String GET_ENCOUNTERS = "Received request to get all encounters";
  public static final String GET_ENCOUNTER_BY_ID(Long id){
    return "Received request to get encounter by id: " + id;
  }
  public static final String POST_ENCOUNTER = "Received request to post encounter";

  public static final String UPDATE_ENCOUNTER(Long id){
    return "Received request to update encounter: " + id;
  }
  public static final String DELETE_ENCOUNTER(Long id){
    return "Received request to delete encounter: " + id;
  }
  //Rental Logging Constants
  public static final String GET_PATIENTS = "Received request to get all patients";
  public static final String GET_PATIENT_BY_ID(Long id){
    return "Received request to get patient by id: " + id;
  }
  public static final String POST_PATIENT = "Received request to post patient";

  public static final String UPDATE_PATIENT(Long id){
    return "Received request to update patient: " + id;
  }
  public static final String DELETE_PATIENT(Long id){
    return "Received request to delete rental: " + id;
  }
  //Failures
  public static final String GET_BY_ID_FAILURE(Long id){
    return "Get by id failed, it does not exist in the database: " + id;
  }
  public static final String UPDATE_ENCOUNTER_FAILURE = "You cannot update an encounter that does not exist.";
  public static final String DELETE_ENCOUNTER_FAILURE = "You cannot delete an encounter that does not exist.";
  public static final String UPDATE_PATIENT_FAILURE = "You cannot update a patient that does not exist.";
  public static final String DELETE_PATIENT_FAILURE = "You cannot delete a patient that does not exist.";

}
