package io.catalyte.training.superhealth.constants;

public class Paths {

  // Local
  public static final String PATIENTS_PATH = "/patients";
  public static final String ENCOUNTERS_PATH(Long patientId){
    return PATIENTS_PATH + "/" + patientId + "/encounters";
  }

}
