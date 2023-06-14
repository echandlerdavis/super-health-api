package io.catalyte.training.movierentals.data;

import io.catalyte.training.movierentals.domains.rental.Rental;
import io.catalyte.training.movierentals.domains.rental.RentedMovie;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RentedMovieFactory {

  private static final Random randomGenerator = new Random();

  public static Long getRandomMovieId(){
    return Long.valueOf(randomGenerator.nextInt(20));
  }

  public static int getDaysRented(){
    return randomGenerator.nextInt(30);
  }

  public static RentedMovie createRandomRentedMovie(Rental rental){
    RentedMovie rentedMovie = new RentedMovie();

//    Setters
    rentedMovie.setMovieId(getRandomMovieId());
    rentedMovie.setDaysRented(getDaysRented());
    rentedMovie.setRental(rental);

    return rentedMovie;
  }


  public static Set<RentedMovie> generateRandomRentedMovies(Rental rental){
    Set<RentedMovie> rentedMovieList = new HashSet<>();
    int numberOfMovies = randomGenerator.nextInt(10);

    for(int i = 0; i < numberOfMovies; i++){
      rentedMovieList.add(createRandomRentedMovie(rental));
    }

    return rentedMovieList;
  }

}