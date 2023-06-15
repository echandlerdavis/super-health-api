package io.catalyte.training.movierentals.domains.movie;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class is a representation of a sports apparel product.
 */
@Entity
public class Movie {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String sku;

  private String title;

  private String genre;

  private String director;

  private Double dailyRentalCost;

//  @OneToMany(mappedBy = "product")
//  @OnDelete(action = OnDeleteAction.CASCADE)
//  private List<Review> reviews;

  public Movie() {
  }

  public Movie(String sku, String title, String genre, String director,
      Double dailyRentalCost) {
    this.sku = sku;
    this.title = title;
    this.genre = genre;
    this.director = director;
    this.dailyRentalCost = dailyRentalCost;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getGenre() {
    return genre;
  }

  public void setGenre(String genre) {
    this.genre = genre;
  }

  public String getDirector() {
    return director;
  }

  public void setDirector(String director) {
    this.director = director;
  }

  public Double getDailyRentalCost() {
    return dailyRentalCost;
  }

  public void setDailyRentalCost(Double dailyRentalCost) {
    this.dailyRentalCost = dailyRentalCost;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Movie movie = (Movie) o;
    return sku.equals(movie.sku) && title.equals(movie.title)
        && genre.equals(
        movie.genre) && director.equals(movie.director) && dailyRentalCost.equals(
        movie.dailyRentalCost);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, sku, title, genre, director, dailyRentalCost);
  }

  @Override
  public String toString() {
    return "Movie{" +
        "id=" + id +
        ", sku='" + sku + '\'' +
        ", title='" + title + '\'' +
        ", genre='" + genre + '\'' +
        ", director='" + director + '\'' +
        ", dailyRentalCost=" + dailyRentalCost +
        '}';
  }
}
