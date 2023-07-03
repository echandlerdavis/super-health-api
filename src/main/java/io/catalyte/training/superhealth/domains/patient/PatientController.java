package io.catalyte.training.superhealth.domains.patient;

import static io.catalyte.training.superhealth.constants.Paths.PATIENTS_PATH;

import io.catalyte.training.superhealth.constants.LoggingConstants;
import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes endpoints for the purchase domain
 */
@RestController
@RequestMapping(value = PATIENTS_PATH)
public class PatientController {

  private final PatientService patientService;
  Logger logger = LogManager.getLogger(PatientController.class);

  @Autowired
  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }

  /**
   * Handles a GET request directed at /patients.
   *
   * @return all rentals in database.
   */
  @GetMapping
  public ResponseEntity<List<Patient>> getPatients() {
    logger.info(LoggingConstants.GET_PATIENTS);
    return new ResponseEntity<>(patientService.getPatients(), HttpStatus.OK);
  }

  /**
   * Handles a GET request directed at /patients/emails.
   *
   * @return all rentals in database.
   */
  @GetMapping(value = "/emails")
  public ResponseEntity<HashMap<Long, String>> getPatientEmails() {
    logger.info(LoggingConstants.GET_PATIENT_EMAILS);
    return new ResponseEntity<>(patientService.getPatientEmails(), HttpStatus.OK);
  }

  /**
   * Handles a GET request with an id parameter
   *
   * @param id - id of rental
   * @return a single rental from the rental's id.
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity getPatientById(@PathVariable Long id) {
    logger.info(LoggingConstants.GET_PATIENT_BY_ID(id));
    return new ResponseEntity(patientService.getPatientById(id), HttpStatus.OK);
  }

  /**
   * Handles a POST request to /rentals. This creates a new purchase that gets saved to the
   * database.
   *
   * @param patient - rental to be created
   * @return valid rental that was saved
   */
  @PostMapping
  public ResponseEntity savePatient(@RequestBody Patient patient) {
    logger.info(LoggingConstants.POST_PATIENT);;
    Patient newPatient = patientService.savePatient(patient);
    return new ResponseEntity<>(newPatient, HttpStatus.CREATED);
  }

  /**
   * Handles a PUT request to /rentals/id. This updates an existing rental object that gets saved to the
   * database.
   *
   * @param patient - rental object
   * @param id - id of rental to be updated
   * @return rental updated to database
   */
  @PutMapping(value = "/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Patient> updateRental(@PathVariable Long id, @RequestBody Patient patient){
    logger.info(LoggingConstants.UPDATE_PATIENT(id));
    return new ResponseEntity<>(patientService.updatePatient(id, patient), HttpStatus.OK);
  }

  /**
   * Handles a DELETE request to /patients/id. This deletes an existing rental object.
   *
   * @param id - id of rental to be deleted
   * @return no content response entity
   */
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deletePatientById(@PathVariable Long id){
    logger.info(LoggingConstants.DELETE_PATIENT(id));;
    patientService.deletePatientById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
