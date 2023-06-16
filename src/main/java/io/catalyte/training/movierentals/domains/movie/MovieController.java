package io.catalyte.training.movierentals.domains.movie;

import static io.catalyte.training.movierentals.constants.Paths.MOVIES_PATH;

import io.catalyte.training.movierentals.constants.LoggingConstants;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The ProductController exposes endpoints for product related actions.
 */
@RestController
@RequestMapping(value = MOVIES_PATH)
public class MovieController {

  Logger logger = LogManager.getLogger(MovieController.class);

  @Autowired
  private MovieService movieService;

  /**
   * Handles a GET request to /movies - returns all movies in the database.
   *
   * @return all movies in the database.
   */
  @GetMapping
  public ResponseEntity<List<Movie>> getMovies() {
    logger.info(LoggingConstants.GET_MOVIES);

    return new ResponseEntity<>(movieService.getMovies(), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /movies/{id}- returns a single movie based on an id defined in the
   * path variable
   *
   * @param id- path variable id
   * @return a single movie from the movie's id.
   */
  @GetMapping(value = "/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Movie> getMovieById(@PathVariable Long id) {
    logger.info(LoggingConstants.GET_MOVIE_BY_ID(id));

    return new ResponseEntity<>(movieService.getMovieById(id), HttpStatus.OK);
  }


  /**
   * Handles a POST request to /movies. This creates a new movie object that gets saved to the
   * database.
   *
   * @param movie - movie object
   * @return movie(s) added to database
   */
  @PostMapping
  public ResponseEntity<Movie> postMovie(@RequestBody Movie movie) {
    logger.info(LoggingConstants.POST_MOVIE);
    return new ResponseEntity<>(movieService.saveMovie(movie), HttpStatus.CREATED);
  }

  /**
   * Handles a PUT request to /movies/id. This updates an existing movie object that gets saved to the
   * database.
   *
   * @param movie - movie object
   * @param id - id of movie to be updated
   * @return movie(s) updated to database
   */
  @PutMapping(value = "/{id}")
  public ResponseEntity<Movie> updateMovie(@PathVariable Long id, @RequestBody Movie movie){
    logger.info(LoggingConstants.UPDATE_MOVIE(id));
    return new ResponseEntity<>(movieService.updateMovie(id, movie), HttpStatus.OK);
  }

  /**
   * Handles a DELETE request to /movies/id. This deletes an existing movie object.
   *
   * @param id - id of movie to be deleted
   * @return no content response entity
   */
  @DeleteMapping(value = "/{id}")
  public ResponseEntity<?> deleteMovie(@PathVariable Long id){
    logger.info(LoggingConstants.DELETE_MOVIE(id));
    movieService.deleteMovie(id);

    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
