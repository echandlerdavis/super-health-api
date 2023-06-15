package io.catalyte.training.movierentals.domains.rental;

import static io.catalyte.training.movierentals.constants.Paths.MOVIES_PATH;
import static io.catalyte.training.movierentals.constants.Paths.RENTALS_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

//  @Test
//  public void savePurchasesWithoutCreditCardReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with credit card number less than 16 digits
//    testRental.setCreditCard(null);
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

//  @Test
//  public void savePurchasesWithoutCardNumberReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with null credit card number
//    testCreditCard.setCardNumber(null);
//    testRental.setCreditCard(testCreditCard);
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

//  @Test
//  public void savePurchasesWithoutCVVReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with credit card number less than 16 digits
//    testCreditCard.setCvv(null);
//    testRental.setCreditCard(testCreditCard);
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
//  public void savePurchasesWithoutExpirationReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with credit card number less than 16 digits
//    testCreditCard.setExpiration(null);
//    testRental.setCreditCard(testCreditCard);
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

//  @Test
//  public void savePurchasesWithCCNumberLessThan16DigitsReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with credit card number less than 16 digits
//    testCreditCard.setCardNumber("123456");
//    testRental.setCreditCard(testCreditCard);
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
//  public void savePurchasesWithCCNumberGreaterThan16DigitsReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with credit card number less than 16 digits
//    testCreditCard.setCardNumber("12345678901234567");
//    testRental.setCreditCard(testCreditCard);
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
//  public void savePurchasesWithCCNumberWithLettersReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with credit card number less than 16 digits
//    testCreditCard.setCardNumber("123456abcde12345");
//    testRental.setCreditCard(testCreditCard);
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
//
//  @Test
//  public void savePurchaseWithCVVLessThan3Digits() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with credit card CVV less than 3 digits
//    testCreditCard.setCvv("01");
//    testRental.setCreditCard(testCreditCard);
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
//  public void savePurchaseWithCVVWithLettersReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set test purchase with credit card CVV less than 3 digits
//    testCreditCard.setCvv("01a");
//    testRental.setCreditCard(testCreditCard);
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
//  public void savePurchaseWithCardWithExpirationDateNotCorrectFormatReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // set test purchase with card that has expiration not in format MM/YY
//    testCreditCard.setExpiration("04/2027");
//    testRental.setCreditCard(testCreditCard);
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
//  public void savePurchaseWithExpiredCardReturns400() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // set test purchase with expired credit card
//    testCreditCard.setExpiration("04/20");
//    testRental.setCreditCard(testCreditCard);
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
//  public void dataBaseFixtureTest() throws Exception {
//    saveTestPurchasesToRepositoryWithDifferentEmails();
//    List<Purchase> testPurchases = purchaseRepository.findAll();
//    assertTrue(testPurchases.size() > 0);
//  }
//
//  @Test
//  public void findPurchasesByEmailReturnsEmailList() throws Exception {
//
//    ObjectMapper mapper = new ObjectMapper();
//
//    for (String email : emails) {
//      MockHttpServletResponse response = mockMvc.perform(get(PURCHASES_PATH + "/" + email))
//          .andReturn().getResponse();
//      List<Purchase> purchases = mapper.readValue(response.getContentAsString(),
//          new TypeReference<List<Purchase>>() {
//          });
//      assertEquals(purchaseCounts.get(email), Integer.valueOf(purchases.size()));
//    }
//
//    String purchasesJson =
//        mockMvc.perform(
//                get(PURCHASES_PATH + "/" + emails[0]))
//            .andReturn()
//            .getResponse()
//            .getContentAsString();
//    Integer numPurchases =
//        mapper.readValue(purchasesJson, new TypeReference<List<Purchase>>() {
//        }).size();
//    assertEquals(Integer.valueOf(2), numPurchases);
//  }
//
//  @Test
//  public void findPurchasesByEmailEmailNotFoundReturnsEmptyList() throws Exception {
//    String purchases = mockMvc.perform(get(PURCHASES_PATH + "/not@anEmail.com"))
//        .andReturn().getResponse().getContentAsString();
//    assertEquals("[]", purchases);
//  }
//
//  @Test
//  public void findPurchasesByEmailEmailNotFoundReturnsOk() throws Exception {
//    mockMvc.perform(get(PURCHASES_PATH + "/not@anEmail.com"))
//        .andExpect(status().isOk());
//  }
//
//  @Test
//  public void getAllPurchasesReturns404() throws Exception {
//    mockMvc.perform(get(PURCHASES_PATH))
//        .andExpect(status().is(404));
//  }
//
//  @Test
//  public void savingPurchaseWithAllInactiveProductsThrowsError() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set all test products to inactive
//    testProducts.forEach(product -> product.setActive(false));
//    movieRepository.saveAll(testProducts);
//
//    // Convert purchase to json string
//    String JsonString = mapper.writeValueAsString(testRental);
//
//    mockMvc.perform(post(PURCHASES_PATH)
//            .content(JsonString)
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  public void savingPurchaseWithOneInactiveProductThrowsError() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set one test product to be inactive
//    testProducts.get(2).setActive(false);
//    movieRepository.save(testProducts.get(2));
//
//    // Convert purchase to json string
//    String JsonString = mapper.writeValueAsString(testRental);
//
//    mockMvc.perform(post(PURCHASES_PATH)
//            .content(JsonString)
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
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
//  public void savingPurchaseIfEveryProductActiveStatusIsNullThrowsError() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set all test products active status to be null
//    testProducts.forEach(product -> product.setActive(null));
//    movieRepository.saveAll(testProducts);
//
//    // Convert purchase to json string
//    String JsonString = mapper.writeValueAsString(testRental);
//
//    mockMvc.perform(post(PURCHASES_PATH)
//            .content(JsonString)
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
//  }
//
//  @Test
//  public void savingPurchaseIfOneProductActiveStatusIsNullThrowsError() throws Exception {
//    // object mapper for creating a json string
//    ObjectMapper mapper = new ObjectMapper();
//
//    // Set one product to null active status
//    testProducts.get(0).setActive(null);
//    movieRepository.save(testProducts.get(0));
//
//    // Convert purchase to json string
//    String JsonString = mapper.writeValueAsString(testRental);
//
//    mockMvc.perform(post(PURCHASES_PATH)
//            .content(JsonString)
//            .contentType(MediaType.APPLICATION_JSON)
//            .accept(MediaType.APPLICATION_JSON))
//        .andExpect(status().isUnprocessableEntity());
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

//
//  @Test
//  public void updateInventoryTest() throws Exception {
//    //This test fails when run with coverage
//    int expectedEndingInventory = INVENTORY_QUANTITY - PURCHASE_QUANTITY;
//    ObjectMapper mapper = new ObjectMapper();
//    mockMvc.perform(
//            post(PURCHASES_PATH)
//                .contentType("application/json")
//                .content(mapper.writeValueAsString(testRental)))
//        .andReturn().getResponse();
//
//    for (Product p : testProducts) {
//      Product updatedProduct = movieRepository.findById(p.getId()).get();
//      assertEquals(Long.valueOf(expectedEndingInventory),
//          Long.valueOf(updatedProduct.getQuantity()));
//    }
//  }
//
//  @Test
//  public void shippingChargeIs0WhenPurchaseAbove50() throws Exception {
//    ObjectMapper mapper = new ObjectMapper();
//    //Update the prices on the purchase items
//    double price = 16.67;
//    int purchaseQty = 1;
//    for (Product p : testProducts) {
//      p.setPrice(price);
//      movieRepository.save(p);
//    }
//    //update the purchase quantities
//    for (LineItem l : testRental.getProducts()) {
//      l.setQuantity(purchaseQty);
//    }
//    //set state
//    testRental.getDeliveryAddress().setDeliveryState(StateEnum.ID.fullName);
//    //save purchase
//    MockHttpServletResponse result = mockMvc.perform(
//            post(PURCHASES_PATH)
//                .contentType("application/json")
//                .content(mapper.writeValueAsString(testRental)))
//        .andReturn().getResponse();
//    String json = result.getContentAsString();
//    Purchase returnedPurchase = mapper.readValue(json, Purchase.class);
//    //assertions
//    assertFalse(returnedPurchase.applyShippingCharge());
//    assertEquals(0.00, returnedPurchase.getShippingCharge(), .001);
//
//  }
//
//  @Test
//  public void shippingChargeIs5WhenPurchaseBelow50AndDeliverToLower48() throws Exception {
//    ObjectMapper mapper = new ObjectMapper();
//    StateEnum state = StateEnum.AL;
//    //Update the prices on the purchase items
//    double price = 16.66;
//    int purchaseQty = 1;
//    for (Product p : testProducts) {
//      p.setPrice(price);
//      movieRepository.save(p);
//    }
//    //update the purchase quantities
//    for (LineItem l : testRental.getProducts()) {
//      l.setQuantity(purchaseQty);
//    }
//    //set state
//    testRental.getDeliveryAddress().setDeliveryState(state.fullName);
//    //save purchase
//    MockHttpServletResponse result = mockMvc.perform(
//            post(PURCHASES_PATH)
//                .contentType("application/json")
//                .content(mapper.writeValueAsString(testRental)))
//        .andReturn().getResponse();
//    String json = result.getContentAsString();
//    Purchase returnedPurchase = mapper.readValue(json, Purchase.class);
//    //assertions
//    assertTrue(returnedPurchase.applyShippingCharge());
//    assertEquals(state.shippingCost, returnedPurchase.getShippingCharge(), .001);
//
//  }
//
//  @Test
//  public void shippingChargeIs10WhenShippingToAlaska() throws Exception {
//    StateEnum state = StateEnum.AK;
//    ObjectMapper mapper = new ObjectMapper();
//    //Update the prices on the purchase items
//    double price = 100.00;
//    int purchaseQty = 1;
//    for (Product p : testProducts) {
//      p.setPrice(price);
//      movieRepository.save(p);
//    }
//    //update the purchase quantities
//    for (LineItem l : testRental.getProducts()) {
//      l.setQuantity(purchaseQty);
//    }
//    //set state
//    testRental.getDeliveryAddress().setDeliveryState(state.fullName);
//    //save purchase
//    MockHttpServletResponse result = mockMvc.perform(
//            post(PURCHASES_PATH)
//                .contentType("application/json")
//                .content(mapper.writeValueAsString(testRental)))
//        .andReturn().getResponse();
//    String json = result.getContentAsString();
//    Purchase returnedPurchase = mapper.readValue(json, Purchase.class);
//    //assertions
//    assertTrue(returnedPurchase.applyShippingCharge());
//    assertEquals(state.shippingCost, returnedPurchase.getShippingCharge(), .001);
//
//  }
//
//  @Test
//  public void shippingChargeIs10WhenShippingToHawaii() throws Exception {
//    StateEnum state = StateEnum.HI;
//    ObjectMapper mapper = new ObjectMapper();
//    //Update the prices on the purchase items
//    double price = 100.00;
//    int purchaseQty = 1;
//    for (Product p : testProducts) {
//      p.setPrice(price);
//      movieRepository.save(p);
//    }
//    //update the purchase quantities
//    for (LineItem l : testRental.getProducts()) {
//      l.setQuantity(purchaseQty);
//    }
//    //set state
//    testRental.getDeliveryAddress().setDeliveryState(state.fullName);
//    //save purchase
//    MockHttpServletResponse result = mockMvc.perform(
//            post(PURCHASES_PATH)
//                .contentType("application/json")
//                .content(mapper.writeValueAsString(testRental)))
//        .andReturn().getResponse();
//    String json = result.getContentAsString();
//    Purchase returnedPurchase = mapper.readValue(json, Purchase.class);
//    //assertions
//    assertTrue(returnedPurchase.applyShippingCharge());
//    assertEquals(state.shippingCost, returnedPurchase.getShippingCharge(), .001);
//
//  }
}
