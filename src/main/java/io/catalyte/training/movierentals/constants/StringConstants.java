package io.catalyte.training.movierentals.constants;

import java.util.List;

public class StringConstants {

  // Exceptions and Errors
  public static final String NOT_FOUND = "404 Not Found";
  public static final String BAD_REQUEST = "400 Bad Request";
  public static final String CONFLICT = "409 Conflict";
  public static final String SERVER_ERROR = "500 An unexpected error occurred.";
  public static final String SERVICE_UNAVAILABLE = "503 Service Unavailable";
  public static final String UNPROCESSABLE_ITEMS = "Unable to process the following Items";

  // Error Messages - Movie Validation
  public static final String MOVIE_RENTAL_COST_INVALID = "Daily rental cost must be a number greater than 0 with 2 digits after the decimal place";
  public static final String MOVIE_SKU_INVALID = "Movie SKU must match the format XXXXXX-DDDD where 'X' is a capital letter and 'D' is a single digit";
  // Error Messages - Purchase Products Validation
  public static final String PRODUCT_INACTIVE = "Product(s) must be active in order to be purchased";
  public static final String RENTAL_HAS_NO_RENTED_MOVIE = "Purchase must have products";
  // Error Messages - Filters
  public static final String UNIMPLEMENTED_FILTERS = "Filters not implemented: ";
  // Google Client ID
  public static final String GOOGLE_CLIENT_ID = "912899852587-7996nh9mlpvpa2446q0il4f9hj5o492h.apps.googleusercontent.com";
  //Header key for google authorization
  public static final String AUTHORIZATION_HEADER = "Authorization";
  //Error Messages - PromotionalCodes
  public static final String INVALID_CODE = "This promotional code is invalid at this time.";
  //Insufficient inventory message
  public static final String INSUFFICIENT_INVENTORY = "There is not enough inventory to cover the order(s) for: ";

  public static final String MOVIE_FIELDS_EMPTY(List<String> emptyFields) {
    String fieldsToString = String.join(", ", emptyFields);
    return "The following fields can not be empty: " + fieldsToString;
  }

  public static final String MOVIE_FIELDS_NULL(List<String> nullFields) {
    String fieldsToString = String.join(", ", nullFields);
    return "The following fields can not be null: " + fieldsToString;
  }
}
