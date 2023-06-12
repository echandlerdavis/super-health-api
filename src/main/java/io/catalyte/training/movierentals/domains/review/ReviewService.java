package io.catalyte.training.movierentals.domains.review;

import java.util.List;

/**
 * This interface provides an abstraction layer for the Reviews Service
 */
public interface ReviewService {

  List<Review> getAllReviewsByProductId(Long productId);
}
