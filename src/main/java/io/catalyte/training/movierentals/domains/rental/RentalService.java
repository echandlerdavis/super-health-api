package io.catalyte.training.movierentals.domains.rental;

import java.util.List;

public interface RentalService {

  List<Rental> getRentals();

  Rental getRentalById(Long id);

  Rental saveRental(Rental rentalToSave);

  Rental updateRental(Long id, Rental rentalToUpdate);

  void deleteRentalById(Long id);

}
