package io.catalyte.training.movierentals.data;


import io.catalyte.training.movierentals.domains.rental.Rental;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.security.core.parameters.P;

public class RentalFactory {

  private static final Random randomGenerator = new Random();


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

  public Rental createRandomRental(){
    Rental rental = new Rental();
//      Setters
    rental.setRentalDate(String.valueOf(
        between(LocalDate.parse("2000-01-01"), LocalDate.now())));
    rental.setRentalTotalCost(generateRandomTotalCost(1.0, 200.0));

    return rental;

  }
  public List<Rental> generateRandomRentalList(int numberOfRentals){
    List<Rental> rentalList = new ArrayList<>();

    for(int i = 0; i < numberOfRentals; i++){
      rentalList.add(createRandomRental());
    }

    return rentalList;
  }

  }


