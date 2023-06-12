package io.catalyte.training.movierentals.data;

import io.catalyte.training.movierentals.domains.movie.Movie;
import io.catalyte.training.movierentals.domains.movie.MovieRepository;
//import io.catalyte.training.movierentals.domains.promotions.PromotionalCode;
//import io.catalyte.training.movierentals.domains.promotions.PromotionalCodeRepository;
//import io.catalyte.training.movierentals.domains.promotions.PromotionalCodeType;
//import io.catalyte.training.movierentals.domains.purchase.LineItem;
//import io.catalyte.training.movierentals.domains.purchase.LineItemRepository;
//import io.catalyte.training.movierentals.domains.purchase.Purchase;
//import io.catalyte.training.movierentals.domains.purchase.PurchaseRepository;
//import io.catalyte.training.movierentals.domains.review.Review;
//import io.catalyte.training.movierentals.domains.review.ReviewRepository;
//import io.catalyte.training.movierentals.domains.user.User;
//import io.catalyte.training.movierentals.domains.user.UserBillingAddress;
//import io.catalyte.training.movierentals.domains.user.UserRepository;
//import java.math.BigDecimal;
//import java.util.Calendar;
//import java.util.Date;
import java.util.List;
import java.util.Random;
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
  private final MovieFactory movieFactory = new MovieFactory();
  @Autowired
  private MovieRepository movieRepository;
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

    // Generate products
    List<Movie> movieList = movieFactory.generateRandomMovie(numberOfMovies);

    // Persist them to the database and save list to purchaseFactory
    logger.info("Loading " + numberOfMovies + " movies...");
    movieRepository.saveAll(movieList);
    logger.info("Data load completed. You can make requests now.");


  }

}
