package io.catalyte.training.movierentals.domains.rental;


import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentedMovieRepository extends JpaRepository<RentedMovie, Long> {
  Set<RentedMovie> findByRental(Rental rental);
}
