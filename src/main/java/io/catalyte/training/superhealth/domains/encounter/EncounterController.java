package io.catalyte.training.superhealth.domains.encounter;

import static io.catalyte.training.superhealth.constants.Paths.ENCOUNTERS_PATH;
import static io.catalyte.training.superhealth.constants.Paths.PATIENTS_PATH;

import io.catalyte.training.superhealth.constants.LoggingConstants;
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
 * The ProductController exposes endpoints for product related actions.
 */
@RestController
@RequestMapping(value = PATIENTS_PATH)
public class EncounterController {

  Logger logger = LogManager.getLogger(EncounterController.class);

  @Autowired
  private EncounterService encounterService;

  /**
   * Handles a GET request to patients/{patientId}/encounters/{id}- returns a single encounter based on an id defined in the
   * path variable
   *
   * @param id- path variable id
   * @return a single movie from the movie's id.
   */
  @GetMapping(value = "{patientId}/encounters/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Encounter> getEncounterById(@PathVariable Long patientId, @PathVariable Long id) {
    logger.info(LoggingConstants.GET_ENCOUNTER_BY_ID(id));

    return new ResponseEntity<>(encounterService.getEncounterById(patientId, id), HttpStatus.OK);
  }


  /**
   * Handles a POST request to /encounters. This creates a new movie object that gets saved to the
   * database.
   *
   * @param encounterDTO - movie object
   * @return encounter added to database
   */
  @PostMapping(value = "{patientId}/encounters")
  public ResponseEntity<Encounter> postEncounter(@PathVariable Long patientId, @RequestBody EncounterDTO encounterDTO) {
    logger.info(LoggingConstants.POST_ENCOUNTER);
    return new ResponseEntity<>(encounterService.saveEncounter(patientId, encounterDTO), HttpStatus.CREATED);
  }
//
//  /**
//   * Handles a PUT request to /encounters/id. This updates an existing encounter object that gets saved to the
//   * database.
//   *
//   * @param encounter - encounter object
//   * @param id - id of encounter to be updated
//   * @return encouter updated to database
//   */
//  @PutMapping(value = "/{id}")
//  public ResponseEntity<Encounter> updateEncounter(@PathVariable Long id,
//      @RequestBody Encounter encounter){
//    logger.info(LoggingConstants.UPDATE_ENCOUNTER(id));
//    return new ResponseEntity<>(encounterService.updateEncounter(id, encounter), HttpStatus.OK);
//  }
//
//  /**
//   * Handles a DELETE request to /encounters/id. This deletes an existing encounter object.
//   *
//   * @param id - id of encounter to be deleted
//   * @return no content response entity
//   */
//  @DeleteMapping(value = "/{id}")
//  public ResponseEntity<?> deleteEncounter(@PathVariable Long id){
//    logger.info(LoggingConstants.DELETE_ENCOUNTER(id));
//    encounterService.deleteEncounter(id);
//
//    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//  }

}
