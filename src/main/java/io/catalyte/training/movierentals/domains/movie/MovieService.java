package io.catalyte.training.movierentals.domains.movie;

import java.util.List;

/**
 * This interface provides an abstraction layer for the Products Service
 */
public interface MovieService {

  List<Movie> getMovies();

  Movie getMovieById(Long id);

  Movie saveMovie(Movie movie);

  Movie updateMovie(Long id, Movie movie);

  void deleteMovie(Long id);


}
