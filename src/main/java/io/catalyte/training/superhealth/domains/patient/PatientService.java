package io.catalyte.training.superhealth.domains.patient;

import java.util.HashMap;
import java.util.List;

public interface PatientService {

  List<Patient> getPatients();

  HashMap<Long, String> getPatientEmails();

  Patient getPatientById(Long id);

  Patient savePatient(Patient rentalToSave);

  Patient updatePatient(Long id, Patient rentalToUpdate);

  void deletePatientById(Long id);

}
