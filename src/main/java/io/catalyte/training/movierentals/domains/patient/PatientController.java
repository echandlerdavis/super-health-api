package io.catalyte.training.movierentals.domains.patient;

import static io.catalyte.training.movierentals.constants.Paths.RENTALS_PATH;

import io.catalyte.training.movierentals.constants.LoggingConstants;
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
@RequestMapping(value = RENTALS_PATH)
public class PatientController {

  private final PatientService patientService;
  Logger logger = LogManager.getLogger(PatientController.class);

  @Autowired
  public PatientController(PatientService patientService) {
    this.patientService = patientService;
  }

  /**
   * Handles a GET request directed at /rentals.
   *
   * @return all rentals in database.
   */
  @GetMapping
  public ResponseEntity<List<Patient>> getRentals() {
    logger.info(LoggingConstants.GET_RENTALS);
    return new ResponseEntity<>(patientService.getRentals(), HttpStatus.OK);
  }

  /**
   * Handles a GET request with an id parameter
   *
   * @param id - id of rental
   * @return a single rental from the rental's id.
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity getRentalById(@PathVariable Long id) {
    logger.info(LoggingConstants.GET_RENTAL_BY_ID(id));
    return new ResponseEntity(patientService.getRentalById(id), HttpStatus.OK);
  }

  /**
   * Handles a POST request to /rentals. This creates a new purchase that gets saved to the
   * database.
   *
   * @param rental - rental to be created
   * @return valid rental that was saved
   */
  @PostMapping
  public ResponseEntity savePurchase(@RequestBody Patient rental) {
    logger.info(LoggingConstants.POST_RENTAL);;
    Patient newRental = patientService.saveRental(rental);
    return new ResponseEntity<>(newRental, HttpStatus.CREATED);
  }

  /**
   * Handles a PUT request to /rentals/id. This updates an existing rental object that gets saved to the
   * database.
   *
   * @param rental - rental object
   * @param id - id of rental to be updated
   * @return rental updated to database
   */
  @PutMapping(value = "/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Patient> updateRental(@PathVariable Long id, @RequestBody Patient rental){
    logger.info(LoggingConstants.UPDATE_RENTAL(id));
    return new ResponseEntity<>(patientService.updateRental(id, rental), HttpStatus.OK);
  }

  /**
   * Handles a DELETE request to /rentals/id. This deletes an existing rental object.
   *
   * @param id - id of rental to be deleted
   * @return no content response entity
   */
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteRentalById(@PathVariable Long id){
    logger.info(LoggingConstants.DELETE_RENTAL(id));;
    patientService.deleteRentalById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
