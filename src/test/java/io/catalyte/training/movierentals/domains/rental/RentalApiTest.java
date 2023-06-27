package io.catalyte.training.movierentals.domains.rental;

import static io.catalyte.training.movierentals.constants.Paths.RENTALS_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.movierentals.constants.StringConstants;
import io.catalyte.training.movierentals.data.MovieFactory;
import io.catalyte.training.movierentals.data.RentalFactory;
import io.catalyte.training.movierentals.data.RentedMovieFactory;
import io.catalyte.training.movierentals.domains.movie.Encounter;
import io.catalyte.training.movierentals.domains.movie.EncounterRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RentalApiTest {

  private final RentalFactory rentalFactory = new RentalFactory();
  private final RentedMovieFactory rentedMovieFactory = new RentedMovieFactory();
  private final MovieFactory movieFactory = new MovieFactory();
  Patient testRental1;
  Patient testRental2;

  List<Encounter> randomMovieList;
  @Autowired
  public PatientRepository patientRepository;
  @Autowired
  public RentedMovieRepository rentedMovieRepository;
  @Autowired
  public EncounterRepository movieRepository;
  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    setTestRentals();
  }

  /**
   * Helper method initializes a test purchase with billing address, delivery address, credit card
   * info, and product with id of 1 to be sent in POST method
   */
  private void setTestRentals() {
    randomMovieList = movieFactory.generateRandomMovieList(20);
    testRental1 = rentalFactory.createRandomRental();
    testRental2 = rentalFactory.createRandomRental();
    List<Patient> testRentals = new ArrayList<>();
    testRentals.add(testRental1);
    testRentals.add(testRental2);

    movieRepository.saveAll(randomMovieList);
    patientRepository.saveAll(testRentals);

    for (Patient rental : testRentals){
      List<RentedMovie> rentedMovieSet = rentedMovieFactory.generateRandomRentedMovies(rental);
      rental.setRentedMovies(rentedMovieSet);
      rentedMovieRepository.saveAll(rentedMovieSet);
    }


  }

  /**
   * Remove rentals that were added in setup.
   */
  @After
  public void tearDown() {
    //delete rentals
    patientRepository.delete(testRental1);
    patientRepository.delete(testRental2);

    //delete rented movies
    rentedMovieRepository.deleteAll();


    //delete movies
    movieRepository.deleteAll(randomMovieList);
  }

  @Test
  public void getRentalsReturns200() throws Exception {
    mockMvc.perform(get(RENTALS_PATH))
        .andExpect(status().isOk());
  }

  @DirtiesContext
  @Test
  public void getRentalByIdReturnsRentalWith200() throws Exception {
    mockMvc.perform(get(RENTALS_PATH + "/" + testRental1.getId().toString()))
        .andExpect(status().isOk());
  }

  @DirtiesContext
  @Test
//  @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
  public void saveRentalReturns201WithRentalObject() throws Exception {
    Patient newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRental.setRentedMovies(newRentedMovies);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
          .contentType("application/json")
          .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    Patient returnedRental = mapper.readValue(response.getContentAsString(), Patient.class);

    assert (returnedRental.equals(newRental));
    assertNotNull(returnedRental.getId());
  }

  @Test
  public void saveRentalReturns400WhenTotalRentalCostIsNegativeNumber() throws Exception{
    Patient newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRental.setRentedMovies(newRentedMovies);
    newRental.setRentalTotalCost(-1.00);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.RENTAL_TOTAL_COST_INVALID));
  }

  @DirtiesContext
  @Test
  public void saveRentalReturns400WhenTotalRentalDateIsInvalid() throws Exception{
    Patient newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRental.setRentedMovies(newRentedMovies);
    newRental.setRentalDate("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.RENTAL_DATE_STRING_INVALID));
  }

  @Test
  public void saveRentalReturns400WhenRentedMoviesAreNull() throws Exception{
    Patient newRental = rentalFactory.createRandomRental();
    newRental.setRentedMovies(null);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
    List<String> errorsList = Arrays.asList(responseErrors);
    assertTrue(errorsList.containsAll(Arrays.asList(
        StringConstants.RENTAL_HAS_NO_RENTED_MOVIE,
        StringConstants.MOVIE_FIELDS_NULL(Arrays.asList("rentedMovies"))
    )));
  }

  @Test
  public void saveRentalReturns400WhenRentedMoviesAreEmpty() throws Exception{
    Patient newRental = rentalFactory.createRandomRental();
    newRental.setRentedMovies(new ArrayList<>());
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.RENTAL_HAS_NO_RENTED_MOVIE));
  }

  @Test
  public void saveRentalReturns400WhenFieldsEmpty() throws Exception {
    Patient newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRental.setRentedMovies(newRentedMovies);
    newRental.setRentalDate("");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.MOVIE_FIELDS_EMPTY(Arrays.asList("rentalDate"))));
  }

  @DirtiesContext
  @Test
  public void saveRentalReturns400WhenFieldsNull() throws Exception{
    Patient newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRental.setRentedMovies(newRentedMovies);
    newRental.setRentalDate(null);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.MOVIE_FIELDS_NULL(Arrays.asList("rentalDate"))));
  }
  @DirtiesContext
  @Test
  public void saveRentalReturns400WhenRentedMovieDaysRentedInvalid() throws Exception{
    Patient newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRentedMovies.get(0).setDaysRented(-1);
    newRental.setRentedMovies(newRentedMovies);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.RENTED_MOVIE_DAYS_RENTED_INVALID));
  }

  @DirtiesContext
  @Test
  public void saveRentalReturns400WhenRentedMovieFieldsNull() throws Exception{
    Patient newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRentedMovies.get(0).setMovieId(null);
    newRental.setRentedMovies(newRentedMovies);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.RENTED_MOVIE_FIELDS_NULL(new HashSet<>(Arrays.asList("movieId")))));
  }

  @Test
  public void saveRentalReturns400WhenRentedMovieMovieIdInvalid() throws Exception{
    Patient newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRentedMovies.get(0).setMovieId(50L);
    newRental.setRentedMovies(newRentedMovies);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants
            .RENTED_MOVIEID_INVALID(Arrays.asList(newRental.getRentedMovies().get(0).getMovieId()))));
  }

  @DirtiesContext
  @Test
  public void updateRentalReturns200WithMovieObject() throws Exception {
    Patient updatedRental = rentalFactory.createRandomRental();
    List<RentedMovie> updatedRentedMovies = rentedMovieFactory.generateRandomRentedMovies(updatedRental);
    updatedRental.setRentedMovies(updatedRentedMovies);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(put(RENTALS_PATH + "/" + testRental1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(updatedRental)))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    Patient returnedRental = mapper.readValue(response.getContentAsString(), Patient.class);

    assert (returnedRental.equals(updatedRental));
    assertNotNull(returnedRental.getId());
  }


  @DirtiesContext
  @Test
  public void DeleteRentalReturns204() throws Exception {
    mockMvc.perform(delete(RENTALS_PATH + "/" + testRental1.getId().toString()))
        .andExpect(status().isNoContent());
  }


}
