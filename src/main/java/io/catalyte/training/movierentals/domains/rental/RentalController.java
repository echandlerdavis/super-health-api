package io.catalyte.training.movierentals.domains.rental;

import static io.catalyte.training.movierentals.constants.Paths.RENTALS_PATH;

import io.catalyte.training.movierentals.domains.movie.Movie;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes endpoints for the purchase domain
 */
@RestController
@RequestMapping(value = RENTALS_PATH)
public class RentalController {

  private final RentalService rentalService;
  Logger logger = LogManager.getLogger(RentalController.class);

  @Autowired
  public RentalController(RentalService rentalService) {
    this.rentalService = rentalService;
  }

  /**
   * Handles a GET request directed at /rentals.
   *
   * @return all rentals in database.
   */
  @GetMapping
  public ResponseEntity<List<Rental>> getRentals() {
    logger.info("Request received for getRentals");
    return new ResponseEntity<>(rentalService.getRentals(), HttpStatus.OK);
  }

  /**
   * Handles a GET request with an id parameter
   *
   * @param id - id of rental
   * @return a single rental from the rental's id.
   */
  @GetMapping(value = "/{id}")
  public ResponseEntity getRentalById(@PathVariable Long id) {
    logger.info("Request received for getRentalById: " + id);
    return new ResponseEntity(rentalService.getRentalById(id), HttpStatus.OK);
  }

  /**
   * Handles a POST request to /rentals. This creates a new purchase that gets saved to the
   * database.
   *
   * @param rental - rental to be created
   * @return valid rental that was saved
   */
  @PostMapping
  public ResponseEntity savePurchase(@RequestBody Rental rental) {
    logger.info("Request received for postRental");
    Rental newRental = rentalService.saveRental(rental);
    return new ResponseEntity<>(newRental, HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Rental> updateRental(@PathVariable Long id, @RequestBody Rental rental){
    logger.info("Request received to updateRental: " + id);
    return new ResponseEntity<>(rentalService.updateRental(id, rental), HttpStatus.OK);
  }

  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteRentalById(@PathVariable Long id){
    logger.info("Request received to delete rental by id: " + id);
    rentalService.deleteRentalById(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
