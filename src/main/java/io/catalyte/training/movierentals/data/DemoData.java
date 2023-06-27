package io.catalyte.training.movierentals.data;

import io.catalyte.training.movierentals.domains.movie.Encounter;
import io.catalyte.training.movierentals.domains.movie.EncounterRepository;
import io.catalyte.training.movierentals.domains.rental.Patient;
import io.catalyte.training.movierentals.domains.rental.PatientRepository;
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

  public static final int DEFAULT_NUMBER_OF_PRODUCTS = 500;
  private final Logger logger = LogManager.getLogger(DemoData.class);
  private final EncounterFactory encounterFactory = new EncounterFactory();
  private final RentalFactory rentalFactory = new RentalFactory();
  private final RentedMovieFactory rentedMovieFactory = new RentedMovieFactory();
  @Autowired
  private EncounterRepository movieRepository;
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
    int numberOfMovies = 20;
    int numberOfRentals = 10;
    // Generate products
    List<Encounter> movieList = encounterFactory.generateRandomMovieList(numberOfMovies);
    List<Patient> rentalList = rentalFactory.generateRandomRentalList(numberOfRentals);

    // Persist them to the database and save list to purchaseFactory
    logger.info("Loading " + numberOfMovies + " movies...");
    movieRepository.saveAll(movieList);
    logger.info("Loading " + numberOfRentals + " rentals...");
    patientRepository.saveAll(rentalList);

//    for (Patient rental : rentalList){
//      List<RentedMovie> rentedMovieSet = rentedMovieFactory.generateRandomRentedMovies(rental);
//      rental.setRentedMovies(rentedMovieSet);
//      rentedMovieRepository.saveAll(rentedMovieSet);
//    }

    logger.info("Data load completed. You can make requests now.");

  }

}
