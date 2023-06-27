package io.catalyte.training.movierentals.domains.rental;

import java.util.List;

public interface PatientService {

  List<Patient> getRentals();

  Patient getRentalById(Long id);

  Patient saveRental(Patient rentalToSave);

  Patient updateRental(Long id, Patient rentalToUpdate);

  void deleteRentalById(Long id);

}
