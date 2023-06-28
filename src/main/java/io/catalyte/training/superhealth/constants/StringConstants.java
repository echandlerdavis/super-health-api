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

  // Error Messages - Movie Validation
  public static final String MOVIE_RENTAL_COST_INVALID = "Daily rental cost must be a number greater than 0 with 2 digits after the decimal place";
  public static final String MOVIE_SKU_INVALID = "Movie SKU must match the format XXXXXX-DDDD where 'X' is a capital letter and 'D' is a single digit";

  public static final String MOVIE_SKU_ALREADY_EXISTS = "Movie SKU already exists";
  // Error Messages - Rented Movies Validation
  public static final String RENTAL_DATE_STRING_INVALID = "Rental Date must match format 'YYYY-MM-DD'";
  public static final String RENTAL_HAS_NO_RENTED_MOVIE = "Rental must have at least one rentedMovie";
  public static final String RENTED_MOVIE_DAYS_RENTED_INVALID = "The number of days rented must be greater than 0";

  public static final String RENTAL_TOTAL_COST_INVALID = "Total rental cost must be a number greater than 0 with 2 digits after the decimal place";

  public static final String RENTED_MOVIEID_INVALID(List<Long> invalidMovieIds){
    String fieldsToString = StringUtils.join(invalidMovieIds, ", ");
    return "The following movie ids do not exist: " + fieldsToString;
  }
  public static final String RENTED_MOVIE_FIELDS_EMPTY(Set<String> emptyFields) {
    String fieldsToString = String.join(", ", emptyFields);
    return "The following rented movie fields cannot be empty: " + fieldsToString;
  }
  public static final String RENTED_MOVIE_FIELDS_NULL(Set<String> nullFields){
    String fieldsToString = String.join(", ", nullFields);
    return "The following rented movie fields cannot be null: " + fieldsToString;
  }
  public static final String MOVIE_FIELDS_EMPTY(List<String> emptyFields) {
    String fieldsToString = String.join(", ", emptyFields);
    return "The following fields cannot be empty: " + fieldsToString;
  }

  public static final String MOVIE_FIELDS_NULL(List<String> nullFields) {
    String fieldsToString = String.join(", ", nullFields);
    return "The following fields cannot be null: " + fieldsToString;
  }
}
