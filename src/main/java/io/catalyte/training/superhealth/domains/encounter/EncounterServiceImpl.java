package io.catalyte.training.superhealth.domains.encounter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.superhealth.constants.LoggingConstants;
import io.catalyte.training.superhealth.constants.StringConstants;
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

  //TODO: Test Validation.
  /**
   * Adds a movie to the database
   *
   * @param encounterDTO - encounter request object
   * @return list of movie objects that are added to database
//   */
  public Encounter saveEncounter(Long patientId, EncounterDTO encounterDTO) {
    if(patientId != encounterDTO.getPatientId()){
      throw new RequestConflict(StringConstants.PATIENT_ID_INVALID);
    }

    List<String> encounterErrors = getEncounterErrors(encounterDTO);
    if (!encounterErrors.isEmpty()) {
      throw new BadRequest(String.join("\n", encounterErrors));
    }

    Encounter newEncounter = new Encounter();
    newEncounter.setPatient(patientService.getPatientById(encounterDTO.getPatientId()));
    newEncounter.setNotes(encounterDTO.getNotes());
    newEncounter.setVisitCode(encounterDTO.getVisitCode());
    newEncounter.setProvider(encounterDTO.getProvider());
    newEncounter.setBillingCode(encounterDTO.getBillingCode());
    newEncounter.setIcd10(encounterDTO.getIcd10());
    newEncounter.setTotalCost(encounterDTO.getTotalCost());
    newEncounter.setCopay(encounterDTO.getCopay());
    newEncounter.setChiefComplaint(encounterDTO.getChiefComplaint());
    newEncounter.setPulse(encounterDTO.getPulse());
    newEncounter.setSystolic(encounterDTO.getSystolic());
    newEncounter.setDiastolic(encounterDTO.getDiastolic());
    newEncounter.setDate(encounterDTO.getDate());

    try {
      return encounterRepository.save(newEncounter);
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

//  //TODO: Update Logging constants for Resource not found, etc.
  public Encounter updateEncounter(Long patientId, Long id, EncounterDTO encounter){
    Encounter findEncounter;

    if(patientId != encounter.getPatientId()){
      throw new RequestConflict(StringConstants.PATIENT_ID_INVALID);
    }

    try {
     findEncounter  = encounterRepository.findById(id).orElse(null);
    }catch(DataAccessException e) {
      logger.error(e.getMessage());
      throw new ResourceNotFound(LoggingConstants.UPDATE_ENCOUNTER_FAILURE);
    }

  if(findEncounter != null) {
    findEncounter.setPatient(patientService.getPatientById(encounter.getPatientId()));
    findEncounter.setNotes(encounter.getNotes());
    findEncounter.setVisitCode(encounter.getVisitCode());
    findEncounter.setProvider(encounter.getProvider());
    findEncounter.setBillingCode(encounter.getBillingCode());
    findEncounter.setIcd10(encounter.getIcd10());
    findEncounter.setTotalCost(encounter.getTotalCost());
    findEncounter.setCopay(encounter.getCopay());
    findEncounter.setChiefComplaint(encounter.getChiefComplaint());
    findEncounter.setPulse(encounter.getPulse());
    findEncounter.setSystolic(encounter.getSystolic());
    findEncounter.setDiastolic(encounter.getDiastolic());
    findEncounter.setDate(encounter.getDate());
  }
    List<String> encounterErrors = getEncounterErrors(encounter);

    if (!encounterErrors.isEmpty()) {
      throw new BadRequest(String.join("\n", encounterErrors));
    }

    try{
      return encounterRepository.save(findEncounter);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }
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
   * @param encounter movie to be validated
   * @return a list of errors
   */
  public List<String> getEncounterErrors(EncounterDTO encounter) {
    List<String> errors = new ArrayList<>();
    List<String> emptyFields = getFieldsEmptyOrNull(encounter).get("emptyFields");
    List<String> nullFields = getFieldsEmptyOrNull(encounter).get("nullFields");

    if (!nullFields.isEmpty()) {
      errors.add(StringConstants.FIELDS_NULL(nullFields));
    }

    if (!emptyFields.isEmpty()) {
      errors.add(StringConstants.FIELDS_EMPTY(emptyFields));
    }

    if(!validateVisitCodeFormat(encounter)){
      errors.add(StringConstants.VISIT_CODE_INVALID);
    }

    if(!validateBillingCode(encounter)){
      errors.add(StringConstants.BILLING_CODE_INVALID);
    }

    if(!validateIcd10(encounter)){
      errors.add(StringConstants.ICD10_INVALID);
    }

    if (!validateCost(encounter.getTotalCost())) {
      errors.add(StringConstants.COST_INVALID("Total cost"));
    }

    if (!validateCost(encounter.getCopay())) {
      errors.add(StringConstants.COST_INVALID("Copay"));
    }

    if(!validateNumber(encounter.getPulse())){
      errors.add(StringConstants.NUMBER_INVALID("Pulse"));
    }

    if(!validateNumber(encounter.getSystolic())){
      errors.add(StringConstants.NUMBER_INVALID("Systolic"));
    }

    if(!validateNumber(encounter.getDiastolic())){
      errors.add(StringConstants.NUMBER_INVALID("Diastolic"));
    }

    if(!validateDateFormat(encounter) &&
        !emptyFields.contains("date") &&
        !nullFields.contains("date")){
    errors.add(StringConstants.DATE_INVALID);
  }

    return errors;
  }



  /**
   * Validates the format of a visit code to match 'LDL DLD' where L is a capital letter
   * and D is a digit.
   *
   * @param encounter encounter to be validated
   * @return boolean if product has valid quantity
   */
  public Boolean validateVisitCodeFormat(EncounterDTO encounter) {
    String regex = "^[A-Z]\\d[A-Z] \\d[A-Z]\\d$";
    Pattern pattern = Pattern.compile(regex);
    if (encounter.getVisitCode() != null) {
      Matcher matcher = pattern.matcher(encounter.getVisitCode());
      return matcher.matches();
    }
    return false;
  };

  /**
   * Validates the format of a billing code to match 'DDD.DDD.DDD-DD' where
   * D is a digit.
   *
   * @param encounter encounter to be validated
   * @return boolean if product has valid quantity
   */
  public Boolean validateBillingCode(EncounterDTO encounter) {
    String regex = "^\\d{3}.\\d{3}.\\d{3}-\\d{2}$";
    Pattern pattern = Pattern.compile(regex);
    if (encounter.getBillingCode() != null) {
      Matcher matcher = pattern.matcher(encounter.getBillingCode());
      return matcher.matches();
    }
    return false;
  };

  /**
   * Validates the format of an icd10 to match 'LDD' where L is a capital letter
   * and D is a digit.
   *
   * @param encounter encounter to be validated
   * @return boolean if product has valid quantity
   */
  public Boolean validateIcd10(EncounterDTO encounter) {
    String regex = "^[A-Z]\\d{2}$";
    Pattern pattern = Pattern.compile(regex);
    if (encounter.getIcd10() != null) {
      Matcher matcher = pattern.matcher(encounter.getIcd10());
      return matcher.matches();
    }
    return false;
  };

  /**
   * Checks that a cost is a double value greater than zero and does not have more than 2 digits after the
   * decimal
   * <p>
   * Because price is stored as a double, regardless of input the product will always have 1 digit
   * after the decimal even if input as an integer, or with 2 zeros after decimal
   *
   * @param cost cost to be validated
   * @return boolean if a cost is valid
   */
  public Boolean validateCost(Double cost) {
    if (cost != null) {
      //Split price by the decimal
      String[] costString = String.valueOf(cost).split("\\.");
      Boolean price2Decimals = costString[1].length() <= 2;
      Boolean priceGreaterThanZero = cost > 0;
      return priceGreaterThanZero || price2Decimals;
    }
    return false;
  }

  /**
   * Reads a movie fields and checks for fields that are empty or null
   *
   * @param encounter movie to be validated
   * @return A Hashmap {"emptyFields": List of empty fields, "nullFields": list of null fields}
   */
  public HashMap<String, List<String>> getFieldsEmptyOrNull(EncounterDTO encounter) {
    List<Field> encounterFields = Arrays.asList(EncounterDTO.class.getDeclaredFields());
    List<String> encounterFieldNames = new ArrayList<>();
    List<String> emptyFields = new ArrayList<>();
    List<String> nullFields = new ArrayList<>();
    HashMap<String, List<String>> results = new HashMap<>();
    //Get product field names
    encounterFields.forEach((field -> encounterFieldNames.add(field.getName())));
    //Remove fields that will be added automatically after the save,  and optional fields
    List<String> optionalFields = new ArrayList<>(
        Arrays.asList("patientId", "notes", "pulse", "systolic", "diastolic")
    );
    encounterFieldNames.removeAll(optionalFields);

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

  public Boolean validateDateFormat(EncounterDTO encounter) {
    String regex = "^\\d{4}-(0[1-9]|1[0-2])-([0-2][0-9]|3[0-1])$";
    Pattern pattern = Pattern.compile(regex);
    if (encounter.getDate() != null) {
      Matcher matcher = pattern.matcher(encounter.getDate());
      return matcher.matches();
    }
    return false;
  }

  public Boolean validateNumber(Integer number){
    if(number == null){
      return true;
    }
    return number > 0;
  }



}