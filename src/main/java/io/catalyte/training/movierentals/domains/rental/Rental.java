package io.catalyte.training.movierentals.domains.rental;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Describes a purchase object that holds the information for a transaction
 */
@Entity
public class Rental {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String rentalDate;

  @OneToMany(mappedBy = "rental")
  @OnDelete(action = OnDeleteAction.CASCADE)
  public List<RentedMovie> rentedMovies;

  private Double rentalTotalCost;

  public Rental() {
  }

  public Rental(Long id, String rentalDate, List<RentedMovie> rentedMovies,
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

  public String getRentalDate() {
    return rentalDate;
  }

  public void setRentalDate(String rentalDate) {
    this.rentalDate = rentalDate;
  }

  public List<RentedMovie> getRentedMovies() {
    return rentedMovies;
  }

  public void setRentedMovies(List<RentedMovie> rentedMovies) {
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
    return rentalDate.equals(rental.rentalDate) && rentedMovies.equals(
        rental.rentedMovies) && rentalTotalCost.equals(rental.rentalTotalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rentalDate, rentedMovies, rentalTotalCost);
  }
}