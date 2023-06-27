package io.catalyte.training.movierentals.domains.patient;

import java.util.List;

public interface PatientService {

  List<Patient> getPatients();

  Patient getPatientById(Long id);

  Patient savePatient(Patient rentalToSave);

  Patient updatePatient(Long id, Patient rentalToUpdate);

  void deletePatientById(Long id);

}
