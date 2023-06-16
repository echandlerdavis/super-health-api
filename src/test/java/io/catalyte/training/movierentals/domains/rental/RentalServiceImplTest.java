package io.catalyte.training.movierentals.domains.rental;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.catalyte.training.movierentals.data.MovieFactory;
import io.catalyte.training.movierentals.data.RentedMovieFactory;
import io.catalyte.training.movierentals.domains.movie.Movie;
import io.catalyte.training.movierentals.domains.movie.MovieRepository;
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
  private final int INVENTORY_QUANTITY = 100;
  private final int PURCHASE_QUANTITY = 1;
  private final long TEST_CODE_RATE = 25;
  Rental testRental;
  List<Rental> testRentals = new ArrayList<>();
  MovieFactory movieFactory;

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
    RentedMovie rentedMovie = new RentedMovie(
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
//  @Test
//  public void savePurchaseReturnsPurchaseForValidInfo() {
//
//    Purchase expected = testPurchase;
//
//    Purchase actual = purchaseServiceImpl.savePurchase(testPurchase);
//    assertEquals(expected, actual);
//  }
//
//  @Test
//  public void savePurchaseIgnoresInvalidPromoCodeAndSaves() {
//    //simulate promocode attached to purchase is not a valid title
//    when(promotionalCodeService.getPromotionalCodeByTitle(anyString())).thenReturn(null);
//    assertNotNull(testPurchase.getPromoCode());
//    purchaseServiceImpl.savePurchase(testPurchase);
//    assertNull(testPurchase.getPromoCode());
//  }
//
//  @Test
//  public void savePurchaseSavesValidPromoCodeFromDataBase() {
//    PromotionalCode fakePromo = new PromotionalCode();
//    fakePromo.setTitle(promoCode.getTitle());
//    fakePromo.setRate(BigDecimal.valueOf(30));
//    fakePromo.setStartDate(promoCode.getStartDate());
//    fakePromo.setEndDate(promoCode.getEndDate());
//    fakePromo.setType(promoCode.getType());
//    testPurchase.setPromoCode(fakePromo);
//    Purchase actualPurchase = purchaseServiceImpl.savePurchase(testPurchase);
//
//    assertEquals(BigDecimal.valueOf(TEST_CODE_RATE), actualPurchase.getPromoCode().getRate());
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCardNumberIsLessThan16Digits() {
//    // arrange
//    testCreditCard.setCardNumber("12345");
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseReturnsThrowsErrorIfCardNumberIsGreaterThan16Digits() {
//    // arrange
//    testCreditCard.setCardNumber("12345678901234567");
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCardNumberContainsLetters() {
//    // arrange
//    testCreditCard.setCardNumber("12345abcde123456");
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCardNumberIsNull() {
//    // arrange
//    testCreditCard.setCardNumber(null);
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCvvIsLessThan3Digits() {
//    // arrange
//    testCreditCard.setCvv("11");
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCvvContainsLetters() {
//    // arrange
//    testCreditCard.setCvv("a2c");
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCvvNull() {
//    // arrange
//    testCreditCard.setCvv(null);
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCardIsExpired() {
//    // arrange
//    testCreditCard.setExpiration("04/23");
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCardExpirationDateIsNull() {
//    // arrange
//    testCreditCard.setExpiration(null);
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCardExpirationDateIsNotCorrectFormat() {
//    // arrange
//    testCreditCard.setExpiration("12/2027");
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCardHolderIsNull() {
//    // arrange
//    testCreditCard.setCardholder(null);
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfCardHolderIsAStringOfOnlyLetters() {
//    // arrange
//    testCreditCard.setCardholder("My N4me");
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfNoCreditCardInfoReceived() {
//    // arrange
//    testCreditCard = new CreditCard();
//    testPurchase.setCreditCard(testCreditCard);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void findByBillingAddressEmailCallsPurchaseService() {
//
//    List<Purchase> actual = purchaseServiceImpl.findByBillingAddressEmail(testEmail);
//    assertEquals(testPurchases, actual);
//  }
//
//  @Test(expected = ServerError.class)
//  public void findByBillingAddressEmailCatchesDataAccessException() {
//    doThrow(new DataAccessException("Test exception") {
//    }).when(purchaseRepository).findByBillingAddressEmail(testEmail);
//
//    List<Purchase> actual = purchaseServiceImpl.findByBillingAddressEmail(testEmail);
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfProductsAreNull() {
//    // arrange
//    testPurchase.setProducts(null);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfProductsIsAnEmptyObject() {
//    Set<LineItem> products = new HashSet<>();
//    // arrange
//    testPurchase.setProducts(products);
//    // act & assert
//    assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfAllProductsAreInactive() {
//    // arrange
//    testProducts.forEach(product -> product.setActive(false));
//
//    // act & assert
//    assertThrows(UnprocessableContent.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfOneProductIsInactive() {
//    // arrange
//    testProducts.get(1).setActive(false);
//    // act & assert
//    assertThrows(UnprocessableContent.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfAllProductActiveStatusIsNull() {
//    // arrange
//    testProducts.forEach(product -> product.setActive(null));
//    // act & assert
//    assertThrows(UnprocessableContent.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test
//  public void savePurchaseThrowsErrorIfOneProductActiveStatusIsNull() {
//    // arrange
//    testProducts.get(1).setActive(null);
//    // act & assert
//    assertThrows(UnprocessableContent.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
//  }
//
//  @Test(expected = ServerError.class)
//  public void lineItemRepositoryErrorThrowsServerError() {
//    doThrow(new DataAccessException("Test exception") {
//    }).when(lineItemRepository).save(any(LineItem.class));
//    Purchase savedPurchase = purchaseServiceImpl.savePurchase(testPurchase);
//    fail(); //this should never run
//  }
//
//  @Test(expected = ServerError.class)
//  public void savePurchaseThrowsServerError() {
//    doThrow(new DataAccessException("Test exception") {
//    }).when(purchaseRepository).save(testPurchase);
//    Purchase copy = purchaseServiceImpl.savePurchase(testPurchase);
//    fail(); //this should never run
//  }
//
//  @Test
//  public void purchaseCalcLineItemTotalSingleItemTest() {
//    final double PRICE = 1.00;
//    final int QUANTITY = 49;
//    DeliveryAddress delivery = new DeliveryAddress();
//    delivery.setDeliveryState(StateEnum.WA.fullName);
//    Set<LineItem> lineItems = new HashSet<>();
//    Product product1 = new Product();
//    product1.setPrice(PRICE);
//    LineItem line1 = new LineItem();
//    line1.setProduct(product1);
//    line1.setQuantity(QUANTITY);
//    lineItems.add(line1);
//    Purchase purchase = new Purchase();
//    purchase.setProducts(lineItems);
//    purchase.setDeliveryAddress(delivery);
//
//    assertEquals(PRICE * QUANTITY, purchase.calcLineItemTotal(), .001);
//    assertTrue(purchase.applyShippingCharge());
//
//  }
//
//  @Test
//  public void purchaseCalcLineItemTotalMultipleItemTest() {
//    final double PRICE = 1.01;
//    final int QUANTITY = 25;
//    Set<LineItem> lineItems = new HashSet<>();
//    Product product1 = new Product();
//    Product product2 = new Product();
//    product1.setPrice(PRICE);
//    product2.setPrice(PRICE);
//    product1.setBrand("brand1");
//    product2.setBrand("different brand");
//    LineItem line1 = new LineItem();
//    LineItem line2 = new LineItem();
//
//    line1.setProduct(product1);
//    line1.setQuantity(QUANTITY);
//    line2.setProduct(product2);
//    line2.setQuantity(QUANTITY);
//
//    DeliveryAddress delivery = new DeliveryAddress();
//    delivery.setDeliveryState(StateEnum.RI.fullName);
//
//    lineItems.add(line1);
//    lineItems.add(line2);
//    Purchase purchase = new Purchase();
//    purchase.setProducts(lineItems);
//    purchase.setDeliveryAddress(delivery);
//
//    assertEquals(PRICE * QUANTITY * lineItems.size(), purchase.calcLineItemTotal(), .001);
//    Assertions.assertFalse(purchase.applyShippingCharge());
//
//  }
//
//  @Test(expected = UnprocessableContent.class)
//  public void savePurchaseThrowsUnprocessableContentForNotEnoughInventory() {
//    int purchaseQuantity = INVENTORY_QUANTITY + PURCHASE_QUANTITY;
//    testPurchase.getProducts().iterator().next().setQuantity(purchaseQuantity);
//    purchaseServiceImpl.savePurchase(testPurchase);
//    fail();//shouldn't run
//  }
//
//  @Test(expected = MultipleUnprocessableContent.class)
//  public void savePurchaseThrowsMultipleUnprocessableContentForNotEnoughInventoryAndInactiveProduct() {
//    int purchaseQuantity = INVENTORY_QUANTITY + PURCHASE_QUANTITY;
//    testProducts.get(1).setActive(false);
//    Iterator<LineItem> lines = testPurchase.getProducts().iterator();
//    for (int count = 0; count < testPurchase.getProducts().size(); count++) {
//      LineItem line = lines.next();
//      if (count++ == 0) {
//        line.setQuantity(purchaseQuantity);
//      }
//    }
//    purchaseServiceImpl.savePurchase(testPurchase);
//    fail();//shouldn't run
//  }
//
//  @Test
//  public void purchaseApplyShippingChargeAlwaysTrueForAlaska() {
//    final double PRICE = 1.00;
//    final int QUANTITY = 100;
//    DeliveryAddress deliveryAddress = new DeliveryAddress();
//    deliveryAddress.setDeliveryState(StateEnum.AK.fullName);
//    Set<LineItem> lineItems = new HashSet<>();
//    Product product1 = new Product();
//    product1.setPrice(PRICE);
//    LineItem line1 = new LineItem();
//    line1.setProduct(product1);
//    line1.setQuantity(QUANTITY);
//    lineItems.add(line1);
//    Purchase purchase = new Purchase();
//    purchase.setProducts(lineItems);
//    purchase.setDeliveryAddress(deliveryAddress);
//
//    assertTrue(purchase.applyShippingCharge());
//
//  }
//
//  @Test
//  public void purchaseApplyShippingChargeAlwaysTrueForHawaii() {
//    final double PRICE = 1.00;
//    final int QUANTITY = 100;
//    DeliveryAddress deliveryAddress = new DeliveryAddress();
//    deliveryAddress.setDeliveryState(StateEnum.HI.fullName);
//    Set<LineItem> lineItems = new HashSet<>();
//    Product product1 = new Product();
//    product1.setPrice(PRICE);
//    LineItem line1 = new LineItem();
//    line1.setProduct(product1);
//    line1.setQuantity(QUANTITY);
//    lineItems.add(line1);
//    Purchase purchase = new Purchase();
//    purchase.setProducts(lineItems);
//    purchase.setDeliveryAddress(deliveryAddress);
//
//    assertTrue(purchase.applyShippingCharge());
//
//  }
//
}
