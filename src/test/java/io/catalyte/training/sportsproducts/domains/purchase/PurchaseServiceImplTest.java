package io.catalyte.training.sportsproducts.domains.purchase;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import io.catalyte.training.sportsproducts.data.ProductFactory;
import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductRepository;
import io.catalyte.training.sportsproducts.domains.product.ProductService;
import io.catalyte.training.sportsproducts.exceptions.BadRequest;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import io.catalyte.training.sportsproducts.exceptions.UnprocessableContent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
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
    private PurchaseServiceImpl purchaseServiceImpl;

    @Mock
    private PurchaseRepository purchaseRepository;
    @Mock
    private ProductService productService;
    @Mock
    private LineItemRepository lineItemRepository;
    @Mock
    private ProductRepository productRepository;

    private ProductFactory productFactory = new ProductFactory();

    private List<Product> testProducts;

    CreditCard testCreditCard = new CreditCard("1234567890123456", "111", "04/30", "Visa");

    Purchase testPurchase = new Purchase();

    String testEmail = "test@validEmail.com";

    ArrayList<Purchase> testPurchases = new ArrayList<>();

    @Before
    public void setUp() {

        //Initialize Mocks
        MockitoAnnotations.initMocks(this);

        // Generate list of test products to add to a purchase
        productFactory = new ProductFactory();
        testProducts = productFactory.generateRandomProducts(3);


        // Initialize a test purchase instance and list of purchases
        setTestPurchase();
        testPurchases.add(testPurchase);

        // Set repository to return list of test purchases when calling findByBillingAddressEmail
        when(purchaseRepository.findByBillingAddressEmail(anyString())).thenReturn(testPurchases);

        // Set consecutive mock calls for product service since Purchase service consecutively calls this for each item in a purchase
        when(productService.getProductById(any()))
                .thenReturn(testProducts.get(0))
                .thenReturn(testProducts.get(1))
                .thenReturn(testProducts.get(2));

        when(productService.getProductsByIds(any())).thenReturn(testProducts);

        //Set repository to return a copy of testPurchase with an id when calling save
        when(purchaseRepository.save(any(Purchase.class))).thenAnswer((p) ->{
            Purchase copyPurchase = new Purchase();
            Purchase passedPurchase = p.getArgument(0);
            copyPurchase.setId(9L);
            copyPurchase.setCreditCard(passedPurchase.getCreditCard());
            copyPurchase.setBillingAddress(passedPurchase.getBillingAddress());
            copyPurchase.setDeliveryAddress(passedPurchase.getDeliveryAddress());

            return copyPurchase;
        });

        //Set lineItemRepository.save to add product to testPurchased
        when(lineItemRepository.findByPurchase(any(Purchase.class))).thenAnswer((l) -> {
            return testPurchase.getProducts();
        });

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

        // Get List of test products to add to purchase
        Set<LineItem> purchasesList = new HashSet<>();
        Long id = 0L;

        for (Product product: testProducts) {
            product.setActive(true);
            product.setId(id);
            ++id;
            LineItem purchaseLineItem = new LineItem();
            purchaseLineItem.setProduct(product);
            purchaseLineItem.setQuantity(1);
            purchasesList.add(purchaseLineItem);
        }

        testPurchase.setProducts(purchasesList);
        testPurchase.setBillingAddress(testBillingAddress);
        testPurchase.setDeliveryAddress(testDeliveryAddress);
        testPurchase.setCreditCard(testCreditCard);
    }

    @Test
    public void savePurchaseReturnsPurchaseForValidInfo(){

        Purchase expected = testPurchase;

        Purchase actual = purchaseServiceImpl.savePurchase(testPurchase);
        assertEquals(expected, actual);
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

    @Test
    public void savePurchaseThrowsErrorIfProductsAreNull() {
        // arrange
        testPurchase.setProducts(null);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfProductsIsAnEmptyObject() {
        Set<LineItem> products = new HashSet<>();
        // arrange
        testPurchase.setProducts(products);
        // act & assert
        assertThrows(BadRequest.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfAllProductsAreInactive() {
        // arrange
        testProducts.forEach(product -> product.setActive(false));

        // act & assert
        assertThrows(UnprocessableContent.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }


    @Test
    public void savePurchaseThrowsErrorIfOneProductIsInactive() {
        // arrange
        testProducts.get(1).setActive(false);
        // act & assert
        assertThrows(UnprocessableContent.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfAllProductActiveStatusIsNull() {
        // arrange
        testProducts.forEach(product -> product.setActive(null));
        // act & assert
        assertThrows(UnprocessableContent.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test
    public void savePurchaseThrowsErrorIfOneProductActiveStatusIsNull() {
        // arrange
        testProducts.get(1).setActive(null);
        // act & assert
        assertThrows(UnprocessableContent.class, () -> purchaseServiceImpl.savePurchase(testPurchase));
    }

    @Test(expected = ServerError.class)
    public void lineItemRepositoryErrorThrowsServerError(){
        doThrow(new DataAccessException("Test exception"){}).when(lineItemRepository).save(any(LineItem.class));
        Purchase savedPurchase = purchaseServiceImpl.savePurchase(testPurchase);
        fail(); //this should never run
    }

    @Test(expected = ServerError.class)
    public void savePurchaseThrowsServerError(){
        doThrow(new DataAccessException("Test exception"){}).when(purchaseRepository).save(testPurchase);
        Purchase copy = purchaseServiceImpl.savePurchase(testPurchase);
        fail(); //this should never run
    }

}
