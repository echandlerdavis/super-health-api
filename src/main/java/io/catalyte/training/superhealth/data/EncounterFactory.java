package io.catalyte.training.superhealth.data;


import io.catalyte.training.superhealth.domains.encounter.Encounter;
import io.catalyte.training.superhealth.domains.patient.Patient;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class EncounterFactory {

  public static final String[] notes = {
      "This is a note about this encounter",
      "Wow, this encounter went so well",
      "This encounter went so so bad",
      "This was a medium encounter",
      "Patient seems very chill and nice and is my favorite patient"
  };

  public static final String[] providers = {
      "Dr. Dolittle",
      "Dr. Zhivago",
      "Nurse Ratchet",
      "Dr. Howser",
      "Dr. House",
      "Dr. Brennan"
  };

  public static final String[] complaints = {
      "headache",
      "backache",
      "stomach ache",
      "broken bone",
      "cold",
      "cough",
      "fever"
  };

  private static final Random randomGenerator = new Random();

  /**
   * Gets a random note between from list.
   *
   * @return - string, a random note
   */
  private static String getRandomNote(){
    return notes[randomGenerator.nextInt(notes.length)];
  };

  /**
   * Gets a random capital letter to apply to visit code and icd10
   *
   * @return a random capital letter
   */
  private static Character getRandomLetter(){
    String letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    return letters.charAt(randomGenerator.nextInt(letters.length()));
  };

  /**
   * Generates a random visit code with format LDL DLD where L is a capital letter and D is a digit.
   * @return
   */
  private static String getRandomVisitCode(){
    String visitCode = "";
    while(visitCode.length() < 6){
      visitCode += getRandomLetter();
      visitCode += String.valueOf(randomGenerator.nextInt(10));
    }
  return visitCode.substring(0, 3) + " " + visitCode.substring(3);
  };

  /**
   * Gets random provider name from the list
   * @return provider string
   */
  private static String getRandomProvider(){
    return providers[randomGenerator.nextInt(providers.length)];
  }

  /**
   * Generates random billing code with format DDD.DDD.DDD-DD
   * @return billing code string
   */
  private static String getRandomBillingCode(){
    int d1 = randomGenerator.nextInt(1000);
    int d2 = randomGenerator.nextInt(1000);
    int d3 = randomGenerator.nextInt(1000);
    int d4 = randomGenerator.nextInt(100);
    return String.format("%03d.%03d.%03d-%02d", d1, d2, d3, d4);

  }

  /**
   * Generates random icd10 with format LDD where L is a capital letter and D is a digit.
   * @return
   */
  private static String getRandomIcd10(){
    return getRandomLetter() + (String.format("%02d", randomGenerator.nextInt(100)));
  }

  /**
   * Generates random double to serve as a price with format to two decimal places
   * @param min minimum value
   * @param max maximum value
   * @return Double
   */
  public static Double generateRandomPrice(double min, double max){
    DecimalFormat df = new DecimalFormat("0.00");
    return Double.valueOf(df.format((randomGenerator.nextDouble() * (max-min)) + min));
  }

  /**
   * Gets random chief complaint from list
   * @return chief complaint string
   */
  public static String getChiefComplaint(){
    return complaints[randomGenerator.nextInt(complaints.length)];
  };

  /**
   * Finds a random date between two date bounds.
   *
   * @param startInclusive - the beginning bound
   * @param endExclusive   - the ending bound
   * @return - a random date as a LocalDate
   */
  private static LocalDate between(LocalDate startInclusive, LocalDate endExclusive) {
    long startEpochDay = startInclusive.toEpochDay();
    long endEpochDay = endExclusive.toEpochDay();
    long randomDay = ThreadLocalRandom
        .current()
        .nextLong(startEpochDay, endEpochDay);

    return LocalDate.ofEpochDay(randomDay);
  }

  /**
   * Orchestrator method to call all random generators and create a unique encounter.
   * @param patient Patient to which the encounter belongs
   * @return Encounter
   */
  public Encounter createRandomEncounter(Patient patient){
    Encounter encounter = new Encounter();
//      Setters
      encounter.setPatient(patient);
      encounter.setNotes(EncounterFactory.getRandomNote());
      encounter.setVisitCode(EncounterFactory.getRandomVisitCode());
      encounter.setProvider(EncounterFactory.getRandomProvider());
      encounter.setBillingCode(EncounterFactory.getRandomBillingCode());
      encounter.setIcd10(EncounterFactory.getRandomIcd10());
      encounter.setTotalCost(EncounterFactory.generateRandomPrice(1.0, 500.0));
      encounter.setCopay(EncounterFactory.generateRandomPrice(1.0, 25.0));
      encounter.setChiefComplaint(EncounterFactory.getChiefComplaint());
      encounter.setPulse(randomGenerator.nextInt(100) + 50);
      encounter.setSystolic(randomGenerator.nextInt(100) + 50);
      encounter.setDiastolic(randomGenerator.nextInt(50) + 50);
      encounter.setDate(String.valueOf(between(LocalDate.parse("2000-01-01"), LocalDate.now())));

    return encounter;

  }

  /**
   * Creates a random number of encounters for a patient. Has to be larger than 1.
   * @param patient Patient to which the encounters belong
   * @return list of encounters.
   */
  public List<Encounter> generateRandomEncounterList(Patient patient){
    List<Encounter> encounterList = new ArrayList<>();
    int numberOfEncounters = randomGenerator.nextInt(5) + 1;
    for(int i = 0; i < numberOfEncounters; i++){
      encounterList.add(createRandomEncounter(patient));
    }

    return encounterList;
  }

  }


