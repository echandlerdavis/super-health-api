package io.catalyte.training.sportsproducts.domains.review;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * The Review controller exposes endpoints for review related actions.
 */
@RestController
@RequestMapping
public class ReviewController {
  Logger logger = LogManager.getLogger(ReviewController.class);
  @Autowired
  private ReviewService reviewService;

  /**
   * Handles a GET request to /products/{productId}/reviews -
   * returns all reviews attributed to a given product.
   *
   * @param productId - the id of the product the reviews belong to
   * @return all reviews in the database attributed to a given product.
   */
  @GetMapping(value = "products/{productId}/reviews")
  public ResponseEntity<List<Review>> getAllReviewsByProductId(@PathVariable(value = "productId") Long productId){
    logger.info("Request received for getAllReviews");
    return new ResponseEntity<>(reviewService.getAllReviewsByProductId(productId), HttpStatus.OK);
  }

}
