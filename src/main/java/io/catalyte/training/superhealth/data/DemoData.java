package io.catalyte.training.superhealth.data;

import io.catalyte.training.superhealth.domains.encounter.EncounterRepository;
import io.catalyte.training.superhealth.domains.patient.Patient;
import io.catalyte.training.superhealth.domains.patient.PatientRepository;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Because this class implements CommandLineRunner, the run method is executed as soon as the server
 * successfully starts and before it begins accepting requests from the outside. Here, we use this
 * as a place to run some code that generates and saves a list of random products into the
 * database.
 */
@Component
public class DemoData implements CommandLineRunner {

  private final Logger logger = LogManager.getLogger(DemoData.class);
  private final PatientFactory patientFactory = new PatientFactory();
//  private final EncounterFactory encounterFactory = new EncounterFactory();
  @Autowired
  private EncounterRepository encounterRepository;
  @Autowired
  private PatientRepository patientRepository;
  @Autowired
  private Environment env;

  @Override
  public void run(String... strings) {
    boolean loadData;

    try {
      // Retrieve the value of custom property in application.yml
      loadData = Boolean.parseBoolean(env.getProperty("movies.load"));
    } catch (NumberFormatException nfe) {
      logger.error("config variable loadData could not be parsed, falling back to default");
      loadData = true;
    }

    if (loadData) {
      seedDatabase();
    }
  }

  private void seedDatabase() {
    int numberOfPatients = 10;

    // Generate patients
    List<Patient> patientList = patientFactory.generateRandomPatientList(numberOfPatients);

    // Persist them to the database and save list to purchaseFactory
    logger.info("Loading " + numberOfPatients + " patients...");
    patientRepository.saveAll(patientList);
    logger.info("Loading random number of encounters...");

//    for (Patient rental : rentalList){
//      List<RentedMovie> rentedMovieSet = rentedMovieFactory.generateRandomRentedMovies(rental);
//      rental.setRentedMovies(rentedMovieSet);
//      rentedMovieRepository.saveAll(rentedMovieSet);
//    }

    logger.info("Data load completed. You can make requests now.");

  }

}
