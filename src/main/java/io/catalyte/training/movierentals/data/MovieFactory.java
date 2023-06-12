package io.catalyte.training.movierentals.data;

import io.catalyte.training.movierentals.domains.movie.Movie;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * This class provides tools for random generation of products.
 */
public class MovieFactory {

  private static final String[] titles = {
      "Grand Budapest Hotel",
      "Avatar",
      "The Little Mermaid",
      "Jaws",
      "Rear Window",
      "The Shawshank Redemption",
      "The Godfather",
      "Caddy Shack",
      "The Lego Movie",
      "Cabin in the Woods",
      "The Babadook"
  };

  private static final String[] genres = {
      "drama",
      "horror",
      "comedy",
      "action",
      "rom-com",
      "fantasy",
      "thriller",
  };
  private static final String[] directors = {
      "Wes Anderson",
      "Alfred Hitchcock",
      "Greta Gerwig",
      "Steven SpielBerg",
      "Quentin Tarantino",
      "Chloe Zhao",
      "Patty Jenkins"
  };



  private static final Random randomGenerator = new Random();

  /**
   * Returns a random brand from the list of titles.
   *
   * @return - a title string
   */
  public static String getTitle() {
    return titles[randomGenerator.nextInt(titles.length)];
  }

  /**
   * Returns a random genre from the list of genres.
   *
   * @return - a genre string
   */
  public static String getGenre() {
    return genres[randomGenerator.nextInt(genres.length)];
  }

  /**
   * Returns a random director from the list of directors.
   *
   * @return - a director string
   */
  public static String getDirector() {
    return directors[randomGenerator.nextInt(directors.length)];
  }

  /**
   * Returns a random double between minimum and maximum parameters to two decimal places.
   *
   * @param min - a double minimum value
   * @param max - a double maximum value
   * @return - a double between minimum and maximum values as the price to two decimal places.
   */
  public static Double getDailyRentalCost(double min, double max) {
    DecimalFormat df = new DecimalFormat("0.00");
    return Double.valueOf(df.format((randomGenerator.nextDouble() * (max - min)) + min));
  }

  /**
   * Generates a random sku.
   *
   * @return - a sku
   */
  public static String getRandomSku() {
    return "ABCDEF-" + RandomStringUtils.random(4, false, true);
  }

  /**
   * Generates a number of random products based on input.
   *
   * @param numberOfMovies - the number of random products to generate
   * @return - a list of random products
   */
  public List<Movie> generateRandomMovie(Integer numberOfMovies) {

    List<Movie> movieList = new ArrayList<>();

    for (int i = 0; i < numberOfMovies; i++) {
      movieList.add(createRandomMovie());
    }

    return movieList;
  }

  /**
   * Uses random generators to build a product.
   *
   * @return - a randomly generated product
   */
  public Movie createRandomMovie() {
    Movie movie = new Movie();
//    Setters
    movie.setTitle(MovieFactory.getTitle());
    movie.setDailyRentalCost(MovieFactory.getDailyRentalCost(1.0, 15.0));
    movie.setDirector(MovieFactory.getDirector());
    movie.setGenre(MovieFactory.getGenre());
    movie.setSku(MovieFactory.getRandomSku());


    return movie;
  }

}
