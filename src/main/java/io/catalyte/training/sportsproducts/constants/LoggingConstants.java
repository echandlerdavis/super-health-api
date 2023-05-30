package io.catalyte.training.sportsproducts.constants;

/**
 * Class for logging statements
 */
public class LoggingConstants {

  //User domain logging statements
  //Google authentication failure
  public static final String GOOGLE_AUTHENTICATION_FAILURE =
      "Email in the request body does not match email from JWT Token";
  public static final String UPDATED_USER_FORMAT = "Updated user %d";
  public static final String UPDATED_LAST_ACTIVE_FORMAT = "Set last active for user %d";
  public static final String NO_EXISTING_USER_FORMAT = "User with id: %d does not exist";
  public static final String SAVED_USER = "Saved user";
  public static final String EMAIL_NULL = "User must have an email";
  public static final String EMAIL_TAKEN = "Email is unavailable";
  public static final String NO_USER_WITH_EMAIL_FORMAT = "User with email %s does not exist";
  public static final String UPDATE_USER_REQUEST = "Request received for updateUser";
  public static final String UPDATE_LAST_ACTIVE = "Request received to update last active time";
  public static final String GET_PROMOCODE_FORMAT = "Get request for promocode %s";
  public static final String POST_PURCHASE = "Received request to post purchase";
  public static final String GET_STATES = "Received request for State options";
  public static final String REJECTED_GET_ALL_PURCHASES = "Rejected request to get all purchases";
  public static final String GET_USER_PURCHASES_FORMAT = "Received request to get all purchases with email %s";

}
