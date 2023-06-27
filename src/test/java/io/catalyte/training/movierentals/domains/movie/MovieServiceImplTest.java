package io.catalyte.training.movierentals.domains.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.catalyte.training.movierentals.data.MovieFactory;
import io.catalyte.training.movierentals.exceptions.BadRequest;
import io.catalyte.training.movierentals.exceptions.RequestConflict;
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
@WebMvcTest(EncounterServiceImpl.class)
public class MovieServiceImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  Encounter testMovie1;
  Encounter testMovie2;
  MovieFactory movieFactory;
  List<Encounter> testMoviesList = new ArrayList<>();
  @InjectMocks
  private EncounterServiceImpl movieServiceImpl;
  @Mock
  private EncounterRepository movieRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    setTestMovies();

    when(movieRepository.findById(anyLong())).thenReturn(Optional.of(testMovie1));
    when(movieRepository.findAll()).thenReturn(testMoviesList);
    when(movieRepository.save(any())).thenReturn(testMovie1);
  }

  private void setTestMovies() {

    // Create Two Random Test Products
    movieFactory = new MovieFactory();
    testMovie1 = new Encounter(
        "ABCDEF-1234",
        "Test Title1",
        "Test Genre1",
        "Test Director1",
        3.45
    );
    testMovie2 = new Encounter(
        "ABCDEF-5678",
        "Test Title2",
        "Test Genre2",
        "Test Director2",
        5.55
    );

    testMoviesList.add(testMovie1);
    testMoviesList.add(testMovie2);

  }

  @Test
  public void getMovieByIdReturnsMovie() {
    Encounter actual = movieServiceImpl.getMovieById(123L);
    assertEquals(testMovie1, actual);
  }

  @Test
  public void getMovieByIdThrowsErrorWhenNotFound() {
    when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> movieServiceImpl.getMovieById(123L));
  }

  @Test
  public void getMovieByIdThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(movieRepository).findById(anyLong());
    assertThrows(ServiceUnavailable.class, () -> movieServiceImpl.getMovieById(123L));
  }
  @Test
  public void getAllMoviesReturnsAllMovies(){
    List<Encounter> actual = movieServiceImpl.getMovies();
    assertEquals(testMoviesList, actual);
  }
  @Test
  public void getAllMoviesThrowsServiceUnavailable(){
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(movieRepository).findAll();
    assertThrows(ServiceUnavailable.class, () -> movieServiceImpl.getMovies());
  }

  @Test
  public void saveValidMovieReturnsMovie() {
    assertEquals(testMovie1, movieServiceImpl.saveMovie(testMovie1));
  }
  @Test
  public void saveMovieThrowsServiceUnavailable() {
    //This test fails when run with coverage
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(movieRepository).save(any());
    assertThrows(ServiceUnavailable.class, () -> movieServiceImpl.saveMovie(testMovie2));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenSkuIsNull(){
    testMovie1.setSku(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenSkuIsEmpty(){
    testMovie1.setSku("");
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenSkuIsInvalidFormat(){
    testMovie1.setSku("ab-123");
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsRequestConflictWhenSkuAlreadyExists(){
    testMovie1.setSku(testMovie2.getSku());
    testMovie1.setId(1L);
    assertThrows(RequestConflict.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }


  @Test
  public void saveMovieThrowsBadRequestWhenTitleIsNull(){
    testMovie1.setTitle(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenTitleIsEmpty(){
    testMovie1.setTitle("");
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenGenreIsNull(){
    testMovie1.setGenre(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenGenreIsEmpty(){
    testMovie1.setGenre("");
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenDailyRentalCostIsNull(){
    testMovie1.setDailyRentalCost(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenDailyRentalCostIsNegative(){
    testMovie1.setDailyRentalCost(-1.00);
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenDailyRentalCostHasMoreThanTwoDecimals(){
    testMovie1.setDailyRentalCost(1.123);
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenDirectorIsNull(){
    testMovie1.setDirector(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void saveMovieThrowsBadRequestWhenDirectorIsEmpty(){
    testMovie1.setDirector("");
    assertThrows(BadRequest.class, () -> movieServiceImpl.saveMovie(testMovie1));
  }

  @Test
  public void updateValidMovieReturnsMovie(){
    testMovie1.setSku("GHIJKL-1234");
    testMovie2.setId(1L);
    testMovie2.setSku("ABCDEF-1234");
   assertEquals(testMovie1, movieServiceImpl.updateMovie(testMovie2.getId(), testMovie1));
  }

  @Test
  public void updateMovieThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(movieRepository).save(any());
    assertThrows(ServiceUnavailable.class, () -> movieServiceImpl.updateMovie(1L, testMovie2));
  }

  @Test
  public void updateMovieByIdThrowsErrorWhenNotFound() {
    when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenSkuIsNull(){
    testMovie1.setSku(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenSkuIsEmpty(){
    testMovie1.setSku("");
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenSkuIsInvalidFormat(){
    testMovie1.setSku("ab-123");
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsRequestConflictWhenSkuAlreadyExists(){
    testMovie1.setSku(testMovie2.getSku());
    testMovie1.setId(1L);
    assertThrows(RequestConflict.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }


  @Test
  public void updateMovieThrowsBadRequestWhenTitleIsNull(){
    testMovie1.setTitle(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenTitleIsEmpty(){
    testMovie1.setTitle("");
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenGenreIsNull(){
    testMovie1.setGenre(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenGenreIsEmpty(){
    testMovie1.setGenre("");
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenDailyRentalCostIsNull(){
    testMovie1.setDailyRentalCost(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenDailyRentalCostIsNegative(){
    testMovie1.setDailyRentalCost(-1.00);
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenDailyRentalCostHasMoreThanTwoDecimals(){
    testMovie1.setDailyRentalCost(1.123);
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenDirectorIsNull(){
    testMovie1.setDirector(null);
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void updateMovieThrowsBadRequestWhenDirectorIsEmpty(){
    testMovie1.setDirector("");
    assertThrows(BadRequest.class, () -> movieServiceImpl.updateMovie(123L, testMovie1));
  }

  @Test
  public void deleteMovieReturnsVoid(){
    movieServiceImpl.deleteMovie(123L);
    verify(movieRepository).deleteById(anyLong());
  }

  @Test
  public void deleteMovieThrowsServiceUnavailable(){
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(movieRepository).deleteById(anyLong());
    assertThrows(ServiceUnavailable.class, () -> movieServiceImpl.deleteMovie(123L));
  }
  @Test
  public void deleteMovieByIdThrowsErrorWhenNotFound() {
    when(movieRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> movieServiceImpl.deleteMovie(123L));
  }
}

