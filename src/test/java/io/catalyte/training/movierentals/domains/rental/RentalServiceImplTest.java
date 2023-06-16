package io.catalyte.training.movierentals.domains.rental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.catalyte.training.movierentals.data.MovieFactory;
import io.catalyte.training.movierentals.domains.movie.Movie;
import io.catalyte.training.movierentals.domains.movie.MovieRepository;
import io.catalyte.training.movierentals.exceptions.BadRequest;
import io.catalyte.training.movierentals.exceptions.ResourceNotFound;
import io.catalyte.training.movierentals.exceptions.ServiceUnavailable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(RentalServiceImpl.class)
public class RentalServiceImplTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  Rental testRental;
  List<Rental> testRentals = new ArrayList<>();
  MovieFactory movieFactory;

  RentedMovie rentedMovie;
  @InjectMocks
  private RentalServiceImpl rentalServiceImpl;
  @Mock
  private RentalRepository rentalRepository;
  @Mock
  private RentedMovieRepository rentedMovieRepository;
  @Mock
  private MovieRepository movieRepository;


  @Before
  public void setUp() {

    //Initialize Mocks
    MockitoAnnotations.initMocks(this);

    //Generate random movies to have movieIds to pull from;
    movieFactory = new MovieFactory();
    List<Movie> movieList = movieFactory.generateRandomMovieList(1);

    // Initialize a test purchase instance and list of purchases
    setTestRental();
    testRentals.add(testRental);

    when(rentalRepository.findAll()).thenReturn(testRentals);
    when(rentalRepository.findById(anyLong())).thenReturn(Optional.of(testRental));
    when(rentalRepository.save(any())).thenReturn(testRental);

    //Set rentedMovieRepository.save to add rentedMovie to testRental
    when(rentedMovieRepository.findByRental(any(Rental.class))).thenAnswer((l) -> {
      return testRental.getRentedMovies();
    });

    //Set movieRepository.findAll() to return a list of movies to cross-reference
    when(movieRepository.findAll()).thenAnswer((l) -> {
      return movieList;
    });

  }

  /**
   * Helper Method to initialize a test purchase with a billing address, delivery address, credit
   * card info, and a random generated product
   */
  private void setTestRental() {
    testRental = new Rental(
        "2023-06-17",
        null,
        10.45
    );

    List<RentedMovie> rentedMovieList = new ArrayList<>();
    rentedMovie = new RentedMovie(
        1L,
        10,
        testRental
        );
    rentedMovieList.add(rentedMovie);
    testRental.setRentedMovies(rentedMovieList);
  }

  @Test
  public void getMovieByIdReturnsRental() {
    Rental actual = rentalServiceImpl.getRentalById(123L);
    assertEquals(testRental, actual);
  }

  @Test
  public void getMovieByIdThrowsErrorWhenNotFound() {
    when(rentalRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> rentalServiceImpl.getRentalById(123L));
  }

  @Test
  public void getMovieByIdThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(rentalRepository).findById(anyLong());
    assertThrows(ServiceUnavailable.class, () -> rentalServiceImpl.getRentalById(123L));
  }

  @Test
  public void getAllRentalsReturnsAllRentals(){
    List<Rental> actual = rentalServiceImpl.getRentals();
    assertEquals(testRentals, actual);
  }

  @Test
  public void getAllMoviesThrowsServiceUnavailable(){
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(rentalRepository).findAll();
    assertThrows(ServiceUnavailable.class, () -> rentalServiceImpl.getRentals());
  }

  @Test
  public void saveValidRentalReturnsRental() {
    assertEquals(testRental, rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsServiceUnavailable() {
    //This test fails when run with coverage
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(rentalRepository).save(any());
    assertThrows(ServiceUnavailable.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsBadRequestWhenRentalDateNull(){
    testRental.setRentalDate(null);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsBadRequestWhenRentalDateEmpty(){
    testRental.setRentalDate("");
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsBadRequestWhenRentalDateIsInvalidFormat(){
    testRental.setRentalDate("Invalid Format");
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsBadRequestWhenRentalTotalCostIsNegative(){
    testRental.setRentalTotalCost(-1.00);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }
  @Test
  public void saveRentalThrowsBadRequestWhenRentalTotalCostMoreThanTwoDecimals(){
    testRental.setRentalTotalCost(1.123);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }
  @Test
  public void saveRentalThrowsBadRequestWhenRentedMoviesAreNull(){
    testRental.setRentedMovies(null);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsBadRequestWhenRentedMoviesAreEmpty(){
    List<RentedMovie> emptyList = new ArrayList<>();
    testRental.setRentedMovies(emptyList);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsBadRequestWhenRentalTotalCostIsNull(){
    testRental.setRentalTotalCost(null);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsBadRequestWhenRentedMovieMovieIdIsNull(){
    rentedMovie.setMovieId(null);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void saveRentalThrowsBadRequestWhenRentedMovieMovieIdDoesNotExist(){
    List<Movie> emptyList = new ArrayList<>();
    when(movieRepository.findAll()).thenAnswer((l) -> {
      return emptyList;
    });
    assertThrows(BadRequest.class, () -> rentalServiceImpl.saveRental(testRental));
  }

  @Test
  public void updateValidRentalReturnsRental(){
    assertEquals(testRental, rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(rentalRepository).save(any());
    assertThrows(ServiceUnavailable.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalRentalThrowsResourceNotFound(){
    when(rentalRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> rentalServiceImpl.updateRental(123L, testRental));
  }

  @Test
  public void updateRentalThrowsBadRequestWhenRentalDateNull(){
    testRental.setRentalDate(null);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalThrowsBadRequestWhenRentalDateEmpty(){
    testRental.setRentalDate("");
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalThrowsBadRequestWhenRentalDateIsInvalidFormat(){
    testRental.setRentalDate("Invalid Format");
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalThrowsBadRequestWhenRentalTotalCostIsNegative(){
    testRental.setRentalTotalCost(-1.00);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }
  @Test
  public void updateRentalThrowsBadRequestWhenRentalTotalCostMoreThanTwoDecimals(){
    testRental.setRentalTotalCost(1.123);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }
  @Test
  public void updateRentalThrowsBadRequestWhenRentedMoviesAreNull(){
    testRental.setRentedMovies(null);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalThrowsBadRequestWhenRentedMoviesAreEmpty(){
    List<RentedMovie> emptyList = new ArrayList<>();
    testRental.setRentedMovies(emptyList);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalThrowsBadRequestWhenRentalTotalCostIsNull(){
    testRental.setRentalTotalCost(null);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalThrowsBadRequestWhenRentedMovieMovieIdIsNull(){
    rentedMovie.setMovieId(null);
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void updateRentalThrowsBadRequestWhenRentedMovieMovieIdDoesNotExist(){
    List<Movie> emptyList = new ArrayList<>();
    when(movieRepository.findAll()).thenAnswer((l) -> {
      return emptyList;
    });
    assertThrows(BadRequest.class, () -> rentalServiceImpl.updateRental(1L, testRental));
  }

  @Test
  public void deleteRentalReturnsVoid(){
    rentalServiceImpl.deleteRentalById(123L);
    verify(rentalRepository).deleteById(anyLong());
  }

  @Test
  public void deleteRentalThrowsServiceUnavailable(){
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(rentalRepository).deleteById(anyLong());
    assertThrows(ServiceUnavailable.class, () -> rentalServiceImpl.deleteRentalById(123L));
  }

  @Test
  public void deleteRentalThrowsResourceNotFound(){
    when(rentalRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> rentalServiceImpl.deleteRentalById(123L));
  }
}
