package io.catalyte.training.movierentals.domains.movie;

import static io.catalyte.training.movierentals.constants.Paths.MOVIES_PATH;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
   * @param movie - optional movie example to be passed
   * @return all movies in the database.
   */
  @GetMapping
  public ResponseEntity<List<Movie>> getMovies(Movie movie) {
    logger.info("Request received for getMovie");

    return new ResponseEntity<>(movieService.getMovies(movie), HttpStatus.OK);
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
    logger.info("Request received for getProductsById: " + id);

    return new ResponseEntity<>(movieService.getMovieById(id), HttpStatus.OK);
  }


  /**
   * Handles a POST request to /movies. This creates a new product object that gets saved to the
   * database.
   *
   * @param movie - product object
   * @return product(s) added to database
   */
  @PostMapping
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Movie> postMovie(@RequestBody Movie movie) {
    logger.info("Request received for postProduct");
    return new ResponseEntity<>(movieService.saveMovie(movie), HttpStatus.CREATED);
  }


}