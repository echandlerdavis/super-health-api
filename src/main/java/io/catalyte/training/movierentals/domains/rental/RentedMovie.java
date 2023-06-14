package io.catalyte.training.movierentals.domains.rental;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Describes one line item of a purchase transaction
 */
@Entity
public class RentedMovie {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JsonIgnore
  private Rental rental;

  private Long movieId;

  private int daysRented;

  public RentedMovie() {
  }

  public RentedMovie(Long id, Long movieId, int daysRented, Rental rental) {
    this.id = id;
    this.movieId = movieId;
    this.daysRented = daysRented;
    this.rental = rental;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getMovieId() {
    return movieId;
  }

  public void setMovieId(Long movieId) {
    this.movieId = movieId;
  }

  public int getDaysRented() {
    return daysRented;
  }

  public void setDaysRented(int daysRented) {
    this.daysRented = daysRented;
  }

  public Rental getRental() {
    return rental;
  }

  public void setRental(Rental rental) {
    this.rental = rental;
  }

  @Override
  public String toString() {
    return "RentedMovie{" +
        "id=" + id +
        ", movieId=" + movieId +
        ", daysRented=" + daysRented +
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
    RentedMovie that = (RentedMovie) o;

    if(movieId != that.movieId){
      return false;
    }
    if(daysRented != that.daysRented){
      return false;
    }
    return Objects.equals(rental, that.rental);

  }

  @Override
  public int hashCode() {
    return Objects.hash(id, movieId, daysRented);
  }
}
