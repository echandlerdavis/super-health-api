package io.catalyte.training.sportsproducts.constants;

public class StringConstants {

  // Exceptions and Errors
  public static final String NOT_FOUND = "404 Not Found";
  public static final String BAD_REQUEST = "400 Bad Request";
  public static final String CONFLICT = "409 Conflict";
  public static final String SERVER_ERROR = "500 An unexpected error occurred.";
  public static final String SERVICE_UNAVAILABLE = "503 Service Unavailable";

  // Errors Messages - Credit Card Validation
  public static final String CARD_NOT_PROVIDED = "Credit was not provided";
  public static final String CARD_NUMBER_INVALID = "Credit card number can not be null and must be 16 digits in length";
  public static final String CARD_CVV_INVALID = "Credit card cvv can not be null and must be 3 digits in length";
  public static final String CARD_HOLDER_INVALID = "Credit card holder can not be null and Can not contain numbers";
  public static final String CARD_EXPIRATION_INVALID_FORMAT = "Credit card expiration date must be in format MM/YY";
  public static final String CARD_EXPIRED = "Credit card is expired";


  // Google Client ID
  public static final String GOOGLE_CLIENT_ID = "912899852587-7996nh9mlpvpa2446q0il4f9hj5o492h.apps.googleusercontent.com";
}
