package io.catalyte.training.movierentals.domains.rental;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Describes one line item of a purchase transaction
 */
@Entity
public class RentedMovie {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long movieId;

  private int daysRented;

  public RentedMovie() {
  }

  public RentedMovie(Long id, Long movieId, int daysRented) {
    this.id = id;
    this.movieId = movieId;
    this.daysRented = daysRented;
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
    return daysRented == that.daysRented && id.equals(that.id) && movieId.equals(that.movieId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, movieId, daysRented);
  }
}
