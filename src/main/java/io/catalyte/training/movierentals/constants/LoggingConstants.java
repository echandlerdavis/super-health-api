package io.catalyte.training.movierentals.constants;

/**
 * Class for logging statements
 */
public class LoggingConstants {
  //Movie endpoints logging constants
  public static final String GET_MOVIES = "Received request to get all movies";
  public static final String GET_MOVIE_BY_ID(Long id){
    return "Received request to get movie by id: " + id;
  }
  public static final String POST_MOVIE = "Received request to post movie";

  public static final String UPDATE_MOVIE(Long id){
    return "Received request to update movie: " + id;
  }
  public static final String DELETE_MOVIE(Long id){
    return "Received request to delete movie: " + id;
  }
  //Rental Logging Constants
  public static final String GET_RENTALS = "Received request to get all rentals";
  public static final String GET_RENTAL_BY_ID(Long id){
    return "Received request to get rental by id: " + id;
  }
  public static final String POST_RENTAL = "Received request to post rental";

  public static final String UPDATE_RENTAL(Long id){
    return "Received request to update rental: " + id;
  }
  public static final String DELETE_RENTAL(Long id){
    return "Received request to delete rental: " + id;
  }
  //Failures
  public static final String GET_BY_ID_FAILURE(Long id){
    return "Get by id failed, it does not exist in the database: " + id;
  }
  public static final String UPDATE_MOVIE_FAILURE = "You cannot update a movie that does not exist.";
  public static final String DELETE_MOVIE_FAILURE = "You cannot delete a movie that does not exist.";
  public static final String UPDATE_RENTAL_FAILURE = "You cannot update a rental that does not exist.";
  public static final String DELETE_RENTAL_FAILURE = "You cannot delete a rental that does not exist.";

}
