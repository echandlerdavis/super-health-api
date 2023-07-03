package io.catalyte.training.superhealth.domains.patient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.superhealth.constants.LoggingConstants;
import io.catalyte.training.superhealth.constants.StringConstants;
import io.catalyte.training.superhealth.domains.encounter.Encounter;
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
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

  private final Logger logger = LogManager.getLogger(PatientServiceImpl.class);

  PatientRepository patientRepository;

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

  public HashMap<Long, String> getPatientEmails(){
    List<Patient> patients;
    try{
      patients = patientRepository.findAll();
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
    HashMap<Long, String> patientEmails = new HashMap<>();
    patients.forEach(patient -> patientEmails.put(patient.getId(), patient.getEmail()));

    return patientEmails;
  };

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

    //set gender to be capitalized correctly if it is not.
    String lowerCaseGender = newPatient.getGender().toLowerCase();
    String formattedGender = lowerCaseGender.substring(0,1).toUpperCase() + lowerCaseGender.substring(1);
    newPatient.setGender(formattedGender);

    //set encounters to an empty array list.
    List<Encounter> encounters = new ArrayList<>();
    newPatient.setEncounters(encounters);

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
    if(patientEmailAlreadyExists(updatedPatient)){
      throw new RequestConflict(StringConstants.EMAIL_ALREADY_EXISTS);
    }

    //set gender to be capitalized correctly if it is not.
    String lowerCaseGender = updatedPatient.getGender().toLowerCase();
    String formattedGender = lowerCaseGender.substring(0,1).toUpperCase() + lowerCaseGender.substring(1);
    updatedPatient.setGender(formattedGender);

    Patient savedPatient;
    findPatient.setId(id);
    findPatient.setFirstName(updatedPatient.getFirstName());
    findPatient.setLastName(updatedPatient.getLastName());
    findPatient.setSsn(updatedPatient.getSsn());
    findPatient.setEmail(updatedPatient.getEmail());
    findPatient.setStreet(updatedPatient.getStreet());
    findPatient.setCity(updatedPatient.getCity());
    findPatient.setState(updatedPatient.getState());
    findPatient.setPostal(updatedPatient.getPostal());
    findPatient.setAge(updatedPatient.getAge());
    findPatient.setHeight(updatedPatient.getHeight());
    findPatient.setWeight(updatedPatient.getWeight());
    findPatient.setInsurance(updatedPatient.getInsurance());
    findPatient.setGender(updatedPatient.getGender());

    try {
      savedPatient = patientRepository.save(findPatient);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
    return savedPatient;
  }

  /**
   * Deletes rental in the database.
   * @param id - id of the rental to be deleted
   */
  public void deletePatientById(Long id){
    Patient findPatient;
    try{
      findPatient = patientRepository.findById(id).orElse(null);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ResourceNotFound(LoggingConstants.UPDATE_PATIENT_FAILURE);
    }
    if(findPatient != null) {
      try {
        patientRepository.deleteById(id);
      } catch (DataAccessException e) {
        logger.error(e.getMessage());
        throw new ServiceUnavailable(e.getMessage());
      }
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
    if(!validateNameFormat(patient.getFirstName()) || !validateNameFormat(patient.getLastName())){
      errors.add(StringConstants.NAME_INVALID);
    }
    if(!validateSSN(patient)){
      errors.add(StringConstants.SSN_INVALID);
    }
    if(!validateEmailFormat(patient)){
      errors.add(StringConstants.EMAIL_INVALID);
    }
    if(!validateStateFormat(patient)){
      errors.add(StringConstants.STATE_INVALID);
    }
    if(!validatePostalCode(patient)){
      errors.add(StringConstants.POSTAL_CODE_INVALID);
    }
    if(!validateNumber(patient.getAge())){
      errors.add(StringConstants.NUMBER_INVALID("Age"));
    }
    if(!validateNumber(patient.getHeight())){
      errors.add(StringConstants.NUMBER_INVALID("Height"));
    }
    if(!validateNumber(patient.getWeight())){
      errors.add(StringConstants.NUMBER_INVALID("Weight"));
    }
    if(!validateGender(patient)){
      errors.add(StringConstants.GENDER_INVALID);
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
    //Remove id as patient will not have an id before it is saved
    patientFieldNames.remove("id");
    //Remove encounters field as it can and should be null
    patientFieldNames.remove("encounters");
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

  public Boolean validateNameFormat(String nameString){
    String regex = "^[a-zA-Z\\s'-]+$";
    Pattern pattern = Pattern.compile(regex);
    if (nameString != null) {
      Matcher matcher = pattern.matcher(nameString);
      return matcher.matches();
    }
    return false;
  };

  public Boolean validateSSN(Patient newPatient){
    String regex = "^\\d{3}-\\d{2}-\\d{4}$";
    Pattern pattern = Pattern.compile(regex);
    if (newPatient.getSsn() != null) {
      Matcher matcher = pattern.matcher(newPatient.getSsn());
      return matcher.matches();
    }
    return false;
  };

  public Boolean validateEmailFormat(Patient newPatient){
    String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z]+\\.[A-Za-z]+$";
    Pattern pattern = Pattern.compile(regex);
    if (newPatient.getEmail() != null) {
      Matcher matcher = pattern.matcher(newPatient.getEmail());
      return matcher.matches();
    }
    return false;

  };

  public Boolean validateStateFormat(Patient newPatient){
    String regex = "^[A-Z]{2}$";
    Pattern pattern = Pattern.compile(regex);
    if (newPatient.getState() != null) {
      Matcher matcher = pattern.matcher(newPatient.getState());
      return matcher.matches();
    }
    return false;
  };

  public Boolean validatePostalCode(Patient newPatient){
    String regex1 = "^\\d{5}$";
    String regex2 = "^\\d{5}-\\d{4}$";
    Pattern pattern1 = Pattern.compile(regex1);
    Pattern pattern2 = Pattern.compile(regex2);
    if (newPatient.getPostal() != null) {
      Matcher matcher1 = pattern1.matcher(newPatient.getPostal());
      Matcher matcher2 = pattern2.matcher(newPatient.getPostal());
      return matcher1.matches() || matcher2.matches();
    }
    return false;
  };

  public Boolean validateNumber(int number){
    return number > 0;
  }

  public Boolean validateGender(Patient newPatient){
    String gender = newPatient.getGender().toLowerCase().trim();
    if(gender.equals("male") || gender.equals("female") || gender.equals("other")){
      return true;
    }
    return false;
  };
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





