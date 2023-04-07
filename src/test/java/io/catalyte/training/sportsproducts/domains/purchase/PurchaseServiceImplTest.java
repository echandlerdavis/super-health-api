package io.catalyte.training.sportsproducts.domains.purchase;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.catalyte.training.sportsproducts.data.ProductFactory;
import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.catalyte.training.sportsproducts.domains.product.ProductService;
import io.catalyte.training.sportsproducts.exceptions.BadRequest;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
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
@WebMvcTest(PurchaseServiceImpl.class)
public class PurchaseServiceImplTest {

    @InjectMocks
    private PurchaseServiceImpl purchaseServiceImpl ;

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private ProductService productService;
    @Mock
    private LineItemRepository lineItemRepository;
    @Mock
    private ProductRepository productRepository;

    @Rule
    public ExpectedException thrown = ExpectedException.none();


    Product testProduct;

    ProductFactory productFactory;

    CreditCard testCreditCard = new CreditCard("1234567890123456", "111", "04/30", "Visa");

    Purchase testPurchase = new Purchase();

    String testEmail = "test@validEmail.com";

    ArrayList<Purchase> testPurchases = new ArrayList<>();

    @Before
    public void setUp() {

        //Initialize Mocks
        MockitoAnnotations.initMocks(this);

        // Set repository to return a random generated product when called to find by id
        productFactory = new ProductFactory();
        testProduct = productFactory.createRandomProduct();
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(testProduct));

        // Initialize a test purchase instance and list of purchases
        setTestPurchase();
        testPurchases.add(testPurchase);

        // Set repository to return list of test purchases when calling findByBillingAddressEmail
        when(purchaseRepository.findByBillingAddressEmail(anyString())).thenReturn(testPurchases);

    }

    /**
     * Helper Method to initialize a test purchase with a billing address, delivery address, credit card info, and a random generated product
     */
    private void setTestPurchase() {
        BillingAddress testBillingAddress = new BillingAddress(
                "123 No Name Street",
                null,
                "No City",
                "Virginia",
                12345,
                testEmail,
                "800-555-5555");

        DeliveryAddress testDeliveryAddress = new DeliveryAddress(
                "first",
                "last",
                "123 No Name Street",
                null,
                "No City",
                "Virginia",
                12345);

        Product product = productRepository.findById(1L).orElse(null);
        LineItem productPurchase = new LineItem();
        productPurchase.setProduct(product);
        Set<LineItem> purchases = Stream.of(productPurchase)
                .collect(Collectors.toCollection(HashSet::new));

        testPurchase.setProducts(purchases);
        testPurchase.setBillingAddress(testBillingAddress);
        testPurchase.setDeliveryAddress(testDeliveryAddress);
        testPurchase.setCreditCard(testCreditCard);
    }

    @Test
    public void savePurchaseReturnsPurchaseForValidInfo(){
        Purchase expected = testPurchase;
        Purchase actual = purchaseServiceImpl.savePurchase(testPurchase);
        assertEquals(expected,actual);
    }

    @Test
    public void savePurchaseThrowsErrorIfCardNumberIsLessThan16Digits() {
        // arrange
        testCreditCard.setCardNumber("12345");
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseReturnsThrowsErrorIfCardNumberIsGreaterThan16Digits() {
        // arrange
        testCreditCard.setCardNumber("12345678901234567");
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCardNumberContainsLetters() {
        // arrange
        testCreditCard.setCardNumber("12345abcde123456");
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCardNumberIsNull() {
        // arrange
        testCreditCard.setCardNumber(null);
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCvvIsLessThan3Digits() {
        // arrange
        testCreditCard.setCvv("11");
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCvvContainsLetters() {
        // arrange
        testCreditCard.setCvv("a2c");
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCvvNull() {
        // arrange
        testCreditCard.setCvv(null);
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCardIsExpired() {
        // arrange
        testCreditCard.setExpiration("04/23");
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCardExpirationDateIsNull() {
        // arrange
        testCreditCard.setExpiration(null);
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCardExpirationDateIsNotCorrectFormat() {
        // arrange
        testCreditCard.setExpiration("12/2027");
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCardHolderIsNull() {
        // arrange
        testCreditCard.setCardholder(null);
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfCardHolderIsAStringOfOnlyLetters() {
        // arrange
        testCreditCard.setCardholder("My N4me");
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfNoCreditCardInfoReceived() {
        // arrange
        testCreditCard = new CreditCard();
        testPurchase.setCreditCard(testCreditCard);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void findByBillingAddressEmailCallsPurchaseService(){

        List<Purchase> actual = purchaseServiceImpl.findByBillingAddressEmail(testEmail);
        assertEquals(testPurchases, actual);
    }

    @Test(expected = ServerError.class)
    public void findByBillingAddressEmailCatchesDataAccessException(){
        doThrow(new DataAccessException("Test exception"){}).when(purchaseRepository).findByBillingAddressEmail(testEmail);

        List<Purchase> actual = purchaseServiceImpl.findByBillingAddressEmail(testEmail);
    }

}
