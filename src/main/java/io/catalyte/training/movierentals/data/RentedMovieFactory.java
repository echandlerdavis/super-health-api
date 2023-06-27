package io.catalyte.training.movierentals.data;

import io.catalyte.training.movierentals.domains.patient.Patient;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RentedMovieFactory {

  private static final Random randomGenerator = new Random();

  public static Long getRandomMovieId(){
    return Long.valueOf(randomGenerator.nextInt(19) + 1);
  }

  public static int getDaysRented(){
    return randomGenerator.nextInt(29) + 1;
  }

  public static RentedMovie createRandomRentedMovie(Patient rental){
    RentedMovie rentedMovie = new RentedMovie();

//    Setters
    rentedMovie.setMovieId(getRandomMovieId());
    rentedMovie.setDaysRented(getDaysRented());
    rentedMovie.setRental(rental);

    return rentedMovie;
  }


  public static List<RentedMovie> generateRandomRentedMovies(Patient rental){
    List<RentedMovie> rentedMovieList = new ArrayList<>();
    int numberOfMovies = randomGenerator.nextInt(9) + 1;

    for(int i = 0; i < numberOfMovies; i++){
      rentedMovieList.add(createRandomRentedMovie(rental));
    }

    return rentedMovieList;
  }

}