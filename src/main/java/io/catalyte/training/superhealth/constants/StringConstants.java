package io.catalyte.training.superhealth.constants;

import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

public class StringConstants {

  // Exceptions and Errors
  public static final String NOT_FOUND = "404 Not Found";
  public static final String BAD_REQUEST = "400 Bad Request";
  public static final String CONFLICT = "409 Conflict";
  public static final String SERVER_ERROR = "500 An unexpected error occurred.";
  public static final String SERVICE_UNAVAILABLE = "503 Service Unavailable";

  // Error Messages - Patient Validation
  public static final String NAME_INVALID = "Name fields must only contain alphabetic characters, an apostrophe, or a hyphen";
  public static final String SSN_INVALID = "Must be a valid SSN with format DDD-DD-DDDD";
  public static final String EMAIL_INVALID = "Email must match the format A@L.L where 'A' any number of alphanumeric characters, and 'L' is any number of alphabetic characters.";
  public static final String STATE_INVALID = "State input must be two capital letters";
  public static final String POSTAL_CODE_INVALID = "Postal input must match format 'DDDDD' or 'DDDDD-DDDD'";
  public static final String NUMBER_INVALID(String field){
    return field + " must be a number larger than zero.";
  };
  public static final String GENDER_INVALID = "Gender must be set to 'Male', 'Female', or 'Other'";
  public static final String EMAIL_ALREADY_EXISTS = "Patient email already exists";

  // Error Messages - Encounters Validation
  public static final String RENTAL_DATE_STRING_INVALID = "Rental Date must match format 'YYYY-MM-DD'";
  public static final String RENTAL_HAS_NO_RENTED_MOVIE = "Rental must have at least one rentedMovie";
  public static final String RENTED_MOVIE_DAYS_RENTED_INVALID = "The number of days rented must be greater than 0";

  public static final String RENTAL_TOTAL_COST_INVALID = "Total rental cost must be a number greater than 0 with 2 digits after the decimal place";

  public static final String RENTED_MOVIEID_INVALID(List<Long> invalidMovieIds){
    String fieldsToString = StringUtils.join(invalidMovieIds, ", ");
    return "The following movie ids do not exist: " + fieldsToString;
  }

  public static final String FIELDS_EMPTY(List<String> emptyFields) {
    String fieldsToString = String.join(", ", emptyFields);
    return "The following fields cannot be empty: " + fieldsToString;
  }

  public static final String FIELDS_NULL(List<String> nullFields) {
    String fieldsToString = String.join(", ", nullFields);
    return "The following fields cannot be null: " + fieldsToString;
  }
}
