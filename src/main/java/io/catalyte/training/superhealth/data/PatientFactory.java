package io.catalyte.training.superhealth.data;

import io.catalyte.training.superhealth.domains.patient.Patient;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * This class provides tools for random generation of products.
 */
public class PatientFactory {

  private static final String[] firstNames = {
      "Megan",
      "Taylor",
      "Chandler",
      "Devin",
      "Hayes",
      "Kathleen",
      "Blake",
      "Alyssa",
      "Britta",
      "Jan"
  };

  private static final String[] lastNames = {
      "Rapinoe",
      "Swift",
      "Davis",
      "Duvall",
      "McCardell",
      "Gorman",
      "Miller",
      "Edwards",
      "Filter",
      "Sport"
  };

  private static final String[] streets = {
      "Oak Ave.",
      "Linden Ln.",
      "Cherry Tree St.",
      "HillView Dr.",
      "Pike St.",
      "Pine St.",
      "Denny St.",
      "Westlake Ave.",
      "Yesler Dr."
  };

  private static final String[] cities = {
      "Wichita",
      "Seattle",
      "San Francisco",
      "Los Angeles",
      "Portland",
      "Highlands",
      "Houston",
      "New Orleans",
      "New York",
      "Buffalo",
      "Philadelphia"
  };

  private static final String[] states = {
      "KS",
      "CA",
      "WA",
      "OR",
      "NC",
      "PA",
      "NY",
      "AZ",
      "TX",
      "MI"
  };

  private static final String[] insurances = {
      "Blue Cross",
      "Molena",
      "Apple Health",
      "Universal Healthcare",
      "Kaiser"
  };

  private static final String[] genders = {
      "Male",
      "Female",
      "Other"
  };

  private static final Random randomGenerator = new Random();

  /**
   * Returns a random first name from the list of first names.
   *
   * @return - a first name string
   */
  public static String getFirstName() {
    return firstNames[randomGenerator.nextInt(firstNames.length)];
  }

  /**
   * Returns a random last name from the list of lastNames.
   *
   * @return - a last name string
   */
  public static String getLastName() {
    return lastNames[randomGenerator.nextInt(lastNames.length)];
  }

  /**
   * Returns a random SSN with format DDD-DD-DDDD.
   *
   * @return - a string with format "DDD-DD-DDDD"
   */
  public static String getRandomSsn() {
    int d1 = randomGenerator.nextInt(1000);
    int d2 = randomGenerator.nextInt(100);
    int d3 = randomGenerator.nextInt(10000);
    return String.format("%03d-%02d-%04d", d1, d2, d3);
  }

  /**
   * Returns a random email from the list of emails.
   *
   * @return - an email string
   */
  public static String getEmail() {
    String domain = "gmail.com";
    String emailAddress = "";
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    while (emailAddress.length() < 5) {
      int character = (int) (Math.random() * 26);
      emailAddress += alphabet.substring(character, character + 1);
      emailAddress += Integer.valueOf((int) (Math.random() * 99)).toString();
      emailAddress += "@" + domain;
    }
    return emailAddress;
  }


  /**
   * Generates a random street.
   *
   * @return - a street
   */
  public static String getRandomStreet() {
    return streets[randomGenerator.nextInt(streets.length)];
  }

  /**
   * Generates a random city.
   *
   * @return - a city
   */
  public static String getRandomCity() {
    return cities[randomGenerator.nextInt(cities.length)];
  }

  /**
   * Generates a random state.
   *
   * @return - a state
   */
  public static String getRandomState() {
    return states[randomGenerator.nextInt(states.length)];
  }

  /**
   * Generates random zip code
   * @return 5 digit zip code as string
   */
  public static String getRandomPostal(){
    int number = randomGenerator.nextInt(100000);
    return String.format("%05d", number);
  }

  /**
   * Gets random insurance from list
   *
   * @return - an insurance string
   */
  public static String getRandomInsurance(){
    return insurances[randomGenerator.nextInt(insurances.length)];
  }

  /**
   * Gets random gender from list
   *
   * @return - a gender string
   */
  public static String getRandomGender(){
    return genders[randomGenerator.nextInt(genders.length)];
  }

  /**
   * Generates a number of random patients based on input.
   *
   * @param numberOfPatients - the number of random patients to generate
   * @return - a list of random patients
   */
  public List<Patient> generateRandomPatientList(Integer numberOfPatients) {

    List<Patient> patientList = new ArrayList<>();

    for (int i = 0; i < numberOfPatients; i++) {
      patientList.add(createRandomPatient());
    }

    return patientList;
  }

  /**
   * Uses random generators to build a patient.
   *
   * @return - a randomly generated patient
   */
  public Patient createRandomPatient() {
    Patient patient = new Patient();
//    Setters
    patient.setFirstName(PatientFactory.getFirstName());
    patient.setLastName(PatientFactory.getLastName());
    patient.setSsn(PatientFactory.getRandomSsn());
    patient.setEmail(PatientFactory.getEmail());
    patient.setStreet(PatientFactory.getRandomStreet());
    patient.setState(PatientFactory.getRandomState());
    patient.setCity(PatientFactory.getRandomCity());
    patient.setPostal(PatientFactory.getRandomPostal());
    patient.setAge(randomGenerator.nextInt(100) + 1);
    patient.setHeight(randomGenerator.nextInt(84));
    patient.setWeight(randomGenerator.nextInt(400));
    patient.setInsurance(PatientFactory.getRandomInsurance());
    patient.setGender(PatientFactory.getRandomGender());

    return patient;
  }

}
