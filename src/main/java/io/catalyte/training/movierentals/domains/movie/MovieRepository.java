package io.catalyte.training.movierentals.domains.movie;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {


//  List<Movie> findByIdIn(List<Long> ids);
}