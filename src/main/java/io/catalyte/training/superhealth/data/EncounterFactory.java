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


  private static final Random randomGenerator = new Random();


  private static String getRandomNote(){
    return notes[randomGenerator.nextInt(notes.length)];
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

  public static Double generateRandomTotalCost(double min, double max){
    DecimalFormat df = new DecimalFormat("0.00");
    return Double.valueOf(df.format((randomGenerator.nextDouble() * (max-min)) + min));
  }

  public Encounter createRandomEncounter(Patient patient){
    Encounter encounter = new Encounter();
//      Setters
      encounter.setPatient(patient);
      encounter.setNotes(EncounterFactory.getRandomNote());
//    rental.setRentalDate(String.valueOf(
//        between(LocalDate.parse("2000-01-01"), LocalDate.now())));
//    rental.setRentalTotalCost(generateRandomTotalCost(1.0, 200.0));

    return encounter;

  }
  public List<Patient> generateRandomRentalList(int numberOfRentals){
    List<Patient> rentalList = new ArrayList<>();

    for(int i = 0; i < numberOfRentals; i++){
      rentalList.add(createRandomRental());
    }

    return rentalList;
  }

  }


