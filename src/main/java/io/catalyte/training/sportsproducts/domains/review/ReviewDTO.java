package io.catalyte.training.sportsproducts.domains.review;

import io.catalyte.training.sportsproducts.domains.product.Product;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * A DTO (Data Transfer Object) representing the input data for creating a review.
 */
public class ReviewDTO {
  @NotBlank
  private String title;
  @NotNull
  @Min(1)
  @Max(5)
  private int rating;
  @NotBlank
  private String review;
  private String createdAt;
  private String userName;
  private Product product;
  public ReviewDTO(){};

  public ReviewDTO(String title, int rating, String review, String createdAt, String userName, Product product) {
    this.title = title;
    this.rating = rating;
    this.review = review;
    this.createdAt = createdAt;
    this.userName = userName;
    this.product = product;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public int getRating() {
    return rating;
  }

  public void setRating(int rating) {
    this.rating = rating;
  }

  public String getReview() {
    return review;
  }

  public void setReview(String review) {
    this.review = review;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUserName() {
    return userName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public Product getProduct() {
    return product;
  }

  public void setProduct(Product product) {
    this.product = product;
  }

}
