package io.catalyte.training.movierentals.domains.rental;


import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RentedMovieRepository extends JpaRepository<RentedMovie, Long> {
  List<RentedMovie> findByRental(Rental rental);
}
