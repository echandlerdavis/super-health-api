package io.catalyte.training.movierentals.domains.rental;

import io.catalyte.training.movierentals.domains.rental.RentedMovie;
import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentedMovieRepository extends JpaRepository<RentedMovie, Long> {

  Set<RentedMovie> findByRental(Rental rental);
  Set<RentedMovie> findByRentalId(Long rentalId);
}
