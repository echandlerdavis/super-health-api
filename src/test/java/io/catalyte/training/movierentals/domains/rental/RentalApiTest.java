package io.catalyte.training.movierentals.domains.rental;

import static io.catalyte.training.movierentals.constants.Paths.MOVIES_PATH;
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
import io.catalyte.training.movierentals.data.MovieFactory;
import io.catalyte.training.movierentals.data.RentalFactory;
import io.catalyte.training.movierentals.data.RentedMovieFactory;
import io.catalyte.training.movierentals.domains.movie.Movie;
import io.catalyte.training.movierentals.domains.movie.MovieRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
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
  Rental testRental1 = rentalFactory.createRandomRental();
  Rental testRental2 = rentalFactory.createRandomRental();
  List<RentedMovie> rentedMovieSet1 = rentedMovieFactory.generateRandomRentedMovies(testRental1);
  List<RentedMovie> rentedMovieSet2 = rentedMovieFactory.generateRandomRentedMovies(testRental2);

  List<Movie> randomMovieList = movieFactory.generateRandomMovieList(20);
  @Autowired
  public RentalRepository rentalRepository;
  @Autowired
  public RentedMovieRepository rentedMovieRepository;
  @Autowired
  public MovieRepository movieRepository;
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
    // Generate random Products and save to repository
    rentalRepository.save(testRental1);
    rentalRepository.save(testRental2);

    testRental1.setRentedMovies(rentedMovieSet1);
    testRental2.setRentedMovies(rentedMovieSet2);

    rentedMovieRepository.saveAll(rentedMovieSet1);
    rentedMovieRepository.saveAll(rentedMovieSet2);

    movieRepository.saveAll(randomMovieList);

  }

  /**
   * Remove rentals that were added in setup.
   */
  @After
  public void tearDown() {
    //delete rentals
    rentalRepository.delete(testRental1);
    rentalRepository.delete(testRental2);

    //delete rented movies
    rentedMovieRepository.deleteAll(rentedMovieSet1);
    rentedMovieRepository.deleteAll(rentedMovieSet2);

    //delete movies
    movieRepository.deleteAll(randomMovieList);
  }

  @Test
  public void getRentalsReturns200() throws Exception {
    mockMvc.perform(get(RENTALS_PATH))
        .andExpect(status().isOk());
  }

  @Test
  public void getRentalByIdReturnsRentalWith200() throws Exception {
    mockMvc.perform(get(RENTALS_PATH + "/"))
        .andExpect(status().isOk());
  }

  @Test
  public void saveRentalReturns201WithRentalObject() throws Exception {
    Rental newRental = rentalFactory.createRandomRental();
    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
    newRental.setRentedMovies(newRentedMovies);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
          .contentType("application/json")
          .content(mapper.writeValueAsString(newRental)))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    Rental returnedRental = mapper.readValue(response.getContentAsString(), Rental.class);

    assert (returnedRental.equals(newRental));
    assertNotNull(returnedRental.getId());
  }


//
//  @Test
//  public void savingPurchaseWithoutAnyProductsThrowsError() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set all test products to be null
//    testRental.setProducts(null);
//
//    // Convert purchase to json string
//    String JsonString = mapper.writeValueAsString(testRental);
//
//    mockMvc.perform(post(PURCHASES_PATH)
//            .content(JsonString)
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isBadRequest());
//  }
//
//  @Test
//  public void postPurchasesReturnsPurchaseObject() throws Exception {
//    //This test fails when run with coverage
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response =
//        mockMvc.perform(
//                post(PURCHASES_PATH)
//                    .contentType("application/json")
//                    .content(mapper.writeValueAsString(testRental)))
//            .andReturn().getResponse();
//
//    Purchase returnedPurchase = mapper.readValue(response.getContentAsString(), Purchase.class);
//
//    assertTrue(twoPurchasesEqualExceptId(testRental, returnedPurchase));
//    assertNotNull(returnedPurchase.getId());
//  }
//



  @Test
  public void UpdateRentalReturns200WithMovieObject() throws Exception {
    Rental updatedRental = rentalFactory.createRandomRental();
    List<RentedMovie> updatedRentedMovies = rentedMovieFactory.generateRandomRentedMovies(updatedRental);
    updatedRental.setRentedMovies(updatedRentedMovies);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(put(RENTALS_PATH + "/1")
            .contentType("application/json")
            .content(mapper.writeValueAsString(updatedRental)))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    Rental returnedRental = mapper.readValue(response.getContentAsString(), Rental.class);

    assert (returnedRental.equals(updatedRental));
    assertNotNull(returnedRental.getId());
  }


  @Test
  public void DeleteRentalReturns204() throws Exception {
    mockMvc.perform(delete(RENTALS_PATH + "/1"))
        .andExpect(status().isNoContent());
  }


}
