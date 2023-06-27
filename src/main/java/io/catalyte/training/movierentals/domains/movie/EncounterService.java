package io.catalyte.training.movierentals.domains.movie;

import java.util.List;

/**
 * This interface provides an abstraction layer for the Products Service
 */
public interface EncounterService {

  List<Encounter> getMovies();

  Encounter getMovieById(Long id);

  Encounter saveMovie(Encounter movie);

  Encounter updateMovie(Long id, Encounter movie);

  void deleteMovie(Long id);


}
