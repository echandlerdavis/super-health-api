package io.catalyte.training.movierentals.data;

import io.catalyte.training.movierentals.domains.encounter.Encounter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * This class provides tools for random generation of products.
 */
public class EncounterFactory {

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
  private static final String[] emails = {
      "megrapinoe@me.com",
      "taylorswift@hotmail.com",
      "chandlerdavis@cataylte.io",
      "devinduvall@catalyte.io",
      "hayesmccardell@catalyte.io",
      "katleengorman@catalyte.io",
      "blakemiller@catalyte.io",
      "alyssaedwards@yahoo.com",
      "brittafilter@cia.gov",
      "jansport@icloud.com"
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

  private static final String[] city = {
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
   * Returns a random brand from the list of titles.
   *
   * @return - a title string
   */
  public static String getFirstName() {
    return firstNames[randomGenerator.nextInt(firstNames.length)];
  }

  /**
   * Returns a random genre from the list of genres.
   *
   * @return - a genre string
   */
  public static String getLastName() {
    return lastNames[randomGenerator.nextInt(lastNames.length)];
  }

  /**
   * Returns a random double between minimum and maximum parameters to two decimal places.
   *
   * @return - a double between minimum and maximum values as the price to two decimal places.
   */
  public static String getRandomSsn() {
    int d1 = randomGenerator.nextInt(1000);
    int d2 = randomGenerator.nextInt(100);
    int d3 = randomGenerator.nextInt(10000);
    return String.format("%03d-%02d-%04d", d1, d2, d3);
  }

  /**
   * Returns a random director from the list of directors.
   *
   * @return - a director string
   */
  public static String getEmail() {
    return emails[randomGenerator.nextInt(emails.length)];
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
   * Generates a random state.
   *
   * @return - a state
   */
  public static String getRandomState() {
    return states[randomGenerator.nextInt(states.length)];
  }

  /**
   * Generates random zip code
   */
  public static String getRandomPostal(){
    int number = randomGenerator.nextInt(100000);
    return String.format("%05d", number);
  }

  public static String getRandomInsurance(){
    return insurances[randomGenerator.nextInt(insurances.length)];
  }

  public static String getRandomGender(){
    return genders[randomGenerator.nextInt(genders.length)];
  }


  /**
   * Generates a number of random products based on input.
   *
   * @param numberOfMovies - the number of random products to generate
   * @return - a list of random products
   */
  public List<Encounter> generateRandomMovieList(Integer numberOfMovies) {

    List<Encounter> movieList = new ArrayList<>();

    for (int i = 0; i < numberOfMovies; i++) {
//      movieList.add(createRandomMovie());
    }

    return movieList;
  }

  /**
   * Uses random generators to build a product.
   *
   * @return - a randomly generated product
   */
//  public Encounter createRandomMovie() {
//    Encounter movie = new Encounter();
////    Setters
//    movie.setTitle(MovieFactory.getTitle());
//    movie.setDailyRentalCost(MovieFactory.getDailyRentalCost(1.0, 15.0));
//    movie.setDirector(MovieFactory.getDirector());
//    movie.setGenre(MovieFactory.getGenre());
//    movie.setSku(MovieFactory.getRandomSku());
//
//
//    return movie;
//  }

}
