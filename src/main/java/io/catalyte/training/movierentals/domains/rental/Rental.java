package io.catalyte.training.movierentals.domains.rental;

import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Describes a purchase object that holds the information for a transaction
 */
@Entity
public class Rental {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String rentalDate;

  private Set<RentedMovie> rentedMovies;

  private Double rentalTotalCost;

  public Rental() {
  }

  public Rental(Long id, String rentalDate, Set<RentedMovie> rentedMovies,
      Double rentalTotalCost) {
    this.id = id;
    this.rentalDate = rentalDate;
    this.rentedMovies = rentedMovies;
    this.rentalTotalCost = rentalTotalCost;
  }


  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }
// TODO: Figure out if you need this.
//  /**
//   * Get the total cost of all the line items
//   *
//   * @return Double
//   */
//  public Double calcRentedMoviesTotal() {
//    return rentedMovies.stream()
//        .map(line -> line.getMovieId().getPrice() * line.getQuantity())
//        .reduce(0.0, (runningTotal, lineTotal) -> runningTotal + lineTotal);
//  }


  public String getRentalDate() {
    return rentalDate;
  }

  public void setRentalDate(String rentalDate) {
    this.rentalDate = rentalDate;
  }

  public Set<RentedMovie> getRentedMovies() {
    return rentedMovies;
  }

  public void setRentedMovies(Set<RentedMovie> rentedMovies) {
    this.rentedMovies = rentedMovies;
  }

  public Double getRentalTotalCost() {
    return rentalTotalCost;
  }

  public void setRentalTotalCost(Double rentalTotalCost) {
    this.rentalTotalCost = rentalTotalCost;
  }

  @Override
  public String toString() {
    return "Rental{" +
        "id=" + id +
        ", rentalDate='" + rentalDate + '\'' +
        ", rentedMovies=" + rentedMovies +
        ", rentalTotalCost=" + rentalTotalCost +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Rental rental = (Rental) o;
    return id.equals(rental.id) && rentalDate.equals(rental.rentalDate) && rentedMovies.equals(
        rental.rentedMovies) && rentalTotalCost.equals(rental.rentalTotalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, rentalDate, rentedMovies, rentalTotalCost);
  }
}