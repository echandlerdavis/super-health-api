package io.catalyte.training.movierentals.domains.purchase;

import static io.catalyte.training.movierentals.constants.LoggingConstants.GET_STATES;
import static io.catalyte.training.movierentals.constants.LoggingConstants.GET_USER_PURCHASES_FORMAT;
import static io.catalyte.training.movierentals.constants.LoggingConstants.POST_PURCHASE;
import static io.catalyte.training.movierentals.constants.LoggingConstants.REJECTED_GET_ALL_PURCHASES;
import static io.catalyte.training.movierentals.constants.Paths.PURCHASES_PATH;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Exposes endpoints for the purchase domain
 */
@RestController
@RequestMapping(value = PURCHASES_PATH)
public class PurchaseController {

  private final PurchaseService purchaseService;
  Logger logger = LogManager.getLogger(PurchaseController.class);

  @Autowired
  public PurchaseController(PurchaseService purchaseService) {
    this.purchaseService = purchaseService;
  }

  /**
   * Handles a POST request to /purchases. This creates a new purchase that gets saved to the
   * database.
   *
   * @param purchase purchase to be created
   * @return valid purchase that was saved
   */
  @PostMapping
  public ResponseEntity savePurchase(@RequestBody Purchase purchase) {
    logger.info(POST_PURCHASE);
    Purchase newPurchase = purchaseService.savePurchase(purchase);
    return new ResponseEntity<>(newPurchase, HttpStatus.CREATED);
  }

  /**
   * Handles a GET request directed at /purchases.
   *
   * @return ResponseEntity with a 404 status code
   */
  @GetMapping
  public ResponseEntity findAllPurchases() {
    logger.error(REJECTED_GET_ALL_PURCHASES);
    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  }

  @GetMapping(value = "/StateOptions")
  public ResponseEntity getStateOptions() {
    logger.info(GET_STATES);
    return new ResponseEntity(purchaseService.getStateOptions(), HttpStatus.OK);
  }

  /**
   * Handles a GET request with an email parameter
   *
   * @param email String email of user whose purchase history should be returned
   * @return ResponseEntity with a list of purchase objects and HttpStatus Ok. If no purchases are
   * found, returns an empty list.
   */
  @RequestMapping(value = "/{email}", method = RequestMethod.GET)
  public ResponseEntity findAllPurchasesByEmail(@PathVariable String email) {
    logger.info(String.format(GET_USER_PURCHASES_FORMAT, email));
    return new ResponseEntity(purchaseService.findByBillingAddressEmail(email), HttpStatus.OK);
  }
}
