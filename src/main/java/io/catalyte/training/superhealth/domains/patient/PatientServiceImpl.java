package io.catalyte.training.superhealth.domains.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.superhealth.constants.LoggingConstants;
import io.catalyte.training.superhealth.constants.StringConstants;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

  private final Logger logger = LogManager.getLogger(PatientServiceImpl.class);

  PatientRepository patientRepository;
//  EncounterRepository encounterRepository;

  @Autowired
  public PatientServiceImpl(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
  }

  /**
   * Retrieves all rentals from the database
   *
   * @return list of rentals
   */
  public List<Patient> getPatients() {
    try {
      return patientRepository.findAll();
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }

  /**
   * Search for single rental by rental id.
   *
   * @param id Long id
   * @return a single rental object.
   */
  public Patient getPatientById(Long id) {
    Patient patient;

    try{
      patient = patientRepository.findById(id).orElse(null);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }

    if(patient !=null){
      return patient;
    } else {
      logger.info(LoggingConstants.GET_BY_ID_FAILURE(id));
      throw new ResourceNotFound(LoggingConstants.GET_BY_ID_FAILURE(id));
    }
  }

  /**
   * Persists a rental to the database
   *
   * @param newPatient - the rental to persist
   * @return the persisted rental object
   */
  public Patient savePatient(Patient newPatient) {
    List<String> patientErrors = getPatientErrors(newPatient);

    if(!patientErrors.isEmpty()){
      throw new BadRequest(String.join("\n", patientErrors));
    }

    if(patientEmailAlreadyExists(newPatient)){
      throw new RequestConflict(StringConstants.EMAIL_ALREADY_EXISTS);
    }

    Patient savedPatient;

    try {
      savedPatient = patientRepository.save(newPatient);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }


    return savedPatient;
  }


  /**
   * Updates and existing rental in the database
   * @param id - id of the existing rental to be updated
   * @param updatedPatient - updated rental request object
   * @return updated Rental object
   */
  public Patient updatePatient(Long id, Patient updatedPatient){
    Patient findPatient;
    try{
      findPatient = patientRepository.findById(id).orElse(null);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ResourceNotFound(LoggingConstants.UPDATE_PATIENT_FAILURE);
    }
    List<String> patientErrors = getPatientErrors(updatedPatient);

    if(!patientErrors.isEmpty()){
      throw new BadRequest(String.join("\n", patientErrors));
    }



    Patient savedPatient;
    findPatient.setId(id);
//    findRental.setRentalDate(updatedRental.getRentalDate());
//    findRental.setRentedMovies(null);
//    findRental.setRentedMovies(updatedRental.getRentedMovies());
//    handleRentedMovies(findRental);
//    findRental.setRentalTotalCost(updatedRental.getRentalTotalCost());
    try {
      savedPatient = patientRepository.save(findPatient);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
    return savedPatient;
  }

  //TODO: Fix this with the find patient thing. check if it's null, etc.
  /**
   * Deletes rental in the database.
   * @param id - id of the rental to be deleted
   */
  public void deletePatientById(Long id){
    try{
      Patient findPatient = patientRepository.findById(id).orElse(null);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ResourceNotFound(LoggingConstants.UPDATE_PATIENT_FAILURE);
    }
    try {
      patientRepository.deleteById(id);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }

  /**
   * Helper method that reads a rental and validates its properties
   *
   * @param patient patient to be validated
   * @return a list of errors
   */
  public List<String> getPatientErrors(Patient patient) {
    List<String> errors = new ArrayList<>();
    List<String> emptyFields = getPatientFieldsEmptyOrNull(patient).get("emptyFields");
    List<String> nullFields = getPatientFieldsEmptyOrNull(patient).get("nullFields");

    if (!nullFields.isEmpty()) {
      errors.add(StringConstants.FIELDS_NULL(nullFields));
    }

    if (!emptyFields.isEmpty()) {
      errors.add(StringConstants.FIELDS_EMPTY(emptyFields));
    }


    return errors;
  }


  /**
   * Reads rental fields and checks for fields that are empty or null
   *
   * @param rental movie to be validated
   * @return A Hashmap {"emptyFields": List of empty fields, "nullFields": list of null fields}
   */
  public HashMap<String, List<String>> getPatientFieldsEmptyOrNull(Patient rental) {
    List<Field> patientFields = Arrays.asList(Patient.class.getDeclaredFields());
    List<String> patientFieldNames = new ArrayList<>();
    List<String> emptyFields = new ArrayList<>();
    List<String> nullFields = new ArrayList<>();
    HashMap<String, List<String>> results = new HashMap<>();
    //Get product field names
    patientFields.forEach((field -> patientFieldNames.add(field.getName())));
    //Remove id as product will not have an id before it is saved
    patientFieldNames.remove("id");
    //Convert rental to a HashMap
    ObjectMapper mapper = new ObjectMapper();
    Map patientMap = mapper.convertValue(rental, HashMap.class);
    //Loop through each fieldName to retrieve each rental mapping value of the field
    patientFieldNames.forEach((field) -> {
      //Check if the value for the rental's field is null or empty and place in the corresponding list
      if (patientMap.get(field) == null) {
        nullFields.add(field);
      } else if (patientMap.get(field).toString().trim() == "") {
        emptyFields.add(field);
      }
    });

    //place each list in the results
    results.put("emptyFields", emptyFields);
    results.put("nullFields", nullFields);
    return results;
  }

  /**
   * Reads and validates nested rented movie objects from a rental attempting to be saved to the
   * database.
   * @param rental - rental to be saved or updated
   * @return list of errors pertaining to the nested rented movie list.
   */
//  private Set<String> getRentedMovieErrors(Patient rental) {
//    // Get rentedMovies from each Rental
//    List<RentedMovie> rentedMovieSet = rental.getRentedMovies();
//    Set<String> rentedMovieErrors = new HashSet<>();
//
//    //Goes through each and adds invalid movie ids to error list.
//    List<Long> invalidMovieIds = new ArrayList<>();
//    Set<String> emptyFields = new HashSet<>();
//    Set<String> nullFields = new HashSet<>();
//    // If no rentedMovies add to error list
//    if (rentedMovieSet == null || rentedMovieSet.size() == 0) {
//      rentedMovieErrors.add(StringConstants.RENTAL_HAS_NO_RENTED_MOVIE);
//    }else {
//      rentedMovieSet.forEach(rentedMovie -> {
//        if (!validateMovieIdExists(rentedMovie) && rentedMovie.getMovieId() != null) {
//          invalidMovieIds.add(rentedMovie.getMovieId());
//        }else if (rentedMovie.getMovieId() == null) {
//          nullFields.add("movieId");
//        } else if (rentedMovie.getMovieId().toString().trim() == "") {
//          emptyFields.add("movieId");
//        }
//        if (rentedMovie.getDaysRented() <= 0) {
//          rentedMovieErrors.add(StringConstants.RENTED_MOVIE_DAYS_RENTED_INVALID);
//        }
//      });
//    }
//
//    if(!invalidMovieIds.isEmpty()){
//      rentedMovieErrors.add(StringConstants.RENTED_MOVIEID_INVALID(invalidMovieIds));
//    }
//
//    if(!emptyFields.isEmpty()){
//      rentedMovieErrors.add(StringConstants.RENTED_MOVIE_FIELDS_EMPTY(emptyFields));
//    }
//
//    if(!nullFields.isEmpty()){
//      rentedMovieErrors.add(StringConstants.RENTED_MOVIE_FIELDS_NULL(nullFields));
//    }
//
//    return rentedMovieErrors;
//    }

  /**
   * Checks the movie repository for a valid movieId from a nested rented movie object.
   * @param rentedMovie - request object with a movie Id
   * @return Boolean if the movieId exists
   */
//  private Boolean validateMovieIdExists(RentedMovie rentedMovie){
//      List<Encounter> allMovies = movieRepository.findAll();
//      for(Encounter movie : allMovies){
//        if(movie.getId() == rentedMovie.getMovieId()){
//          return true;
//        }
//      }
//      return false;
//    }

  /**
   * Checks whether the sku of a movie attempting to be added or updated already exists in the database.
   * @param newPatient - patient to be saved
   * @return Boolean if the sku exists already.
   */
  public Boolean patientEmailAlreadyExists(Patient newPatient){
    List<Patient> allPatients;
    try{
      allPatients = patientRepository.findAll();
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
    if(newPatient.getEmail() != null){
      for(Patient patient : allPatients){
        if (patient.getEmail().equals(newPatient.getEmail()) && !patient.getId().equals(newPatient.getId())) {
        return true;
          }
      }
    }
    return false;
  }

  }





