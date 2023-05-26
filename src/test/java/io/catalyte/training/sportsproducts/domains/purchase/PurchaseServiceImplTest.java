package io.catalyte.training.sportsproducts.domains.purchase;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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
import io.catalyte.training.sportsproducts.domains.promotions.PromotionalCode;
import io.catalyte.training.sportsproducts.domains.promotions.PromotionalCodeService;
import io.catalyte.training.sportsproducts.domains.promotions.PromotionalCodeType;
import io.catalyte.training.sportsproducts.exceptions.BadRequest;
import io.catalyte.training.sportsproducts.exceptions.MultipleUnprocessableContent;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import io.catalyte.training.sportsproducts.exceptions.UnprocessableContent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
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

  private final int INVENTORY_QUANTITY = 100;
  private final int PURCHASE_QUANTITY = 1;
  private final long TEST_CODE_RATE = 25;
  CreditCard testCreditCard = new CreditCard("1234567890123456", "111", "04/30", "Visa");
  Purchase testPurchase = new Purchase();
  String testEmail = "test@validEmail.com";
  PromotionalCode promoCode;
  ArrayList<Purchase> testPurchases = new ArrayList<>();
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
  @Mock
  private PromotionalCodeService promotionalCodeService;
  private ProductFactory productFactory = new ProductFactory();
  private List<Product> testProducts;

  @Before
  public void setUp() {

    //Initialize Mocks
    MockitoAnnotations.initMocks(this);

    // Generate list of test products to add to a purchase
    productFactory = new ProductFactory();
    testProducts = productFactory.generateRandomProducts(3);

    //set the valid promocode
    Long dateMilliseconds = new Date().getTime();
    promoCode = new PromotionalCode(
        "Test",
        "description",
        PromotionalCodeType.FLAT,
        BigDecimal.valueOf(TEST_CODE_RATE),
        new Date(dateMilliseconds),
        new Date(dateMilliseconds * 1000 * 60 * 60 * 24)
    );

    // Initialize a test purchase instance and list of purchases
    setTestPurchase();
    testPurchases.add(testPurchase);

    // Set repository to return list of test purchases when calling findByBillingAddressEmail
    when(purchaseRepository.findByBillingAddressEmail(anyString())).thenReturn(testPurchases);

    // Set consecutive mock calls for product service since Purchase service consecutively calls this for each item in a purchase
    //set mock for productService.getProductsByIds
    when(productService.getProductsByIds(any()))
        .thenReturn(testProducts);

    when(productService.getProductsByIds(any())).thenReturn(testProducts);

    //Set repository to return a copy of testPurchase with an id when calling save
    when(purchaseRepository.save(any(Purchase.class))).thenAnswer((p) -> {
      Purchase copyPurchase = new Purchase();
      Purchase passedPurchase = p.getArgument(0);
      copyPurchase.setId(9L);
      copyPurchase.setCreditCard(passedPurchase.getCreditCard());
      copyPurchase.setBillingAddress(passedPurchase.getBillingAddress());
      copyPurchase.setDeliveryAddress(passedPurchase.getDeliveryAddress());
      copyPurchase.setDate(passedPurchase.getDate());
      copyPurchase.setPromoCode(passedPurchase.getPromoCode());
      copyPurchase.setShippingCharge(passedPurchase.getShippingCharge());

      return copyPurchase;
    });

    //Set promotional code repo to return a valid promocode by default
    when(promotionalCodeService.getPromotionalCodeByTitle(anyString())).thenReturn(promoCode);

    //Set lineItemRepository.save to add product to testPurchased
    when(lineItemRepository.findByPurchase(any(Purchase.class))).thenAnswer((l) -> {
      return testPurchase.getProducts();
    });

  }

  /**
   * Helper Method to initialize a test purchase with a billing address, delivery address, credit
   * card info, and a random generated product
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

    for (Product product : testProducts) {
      product.setActive(true);
      product.setId(id);
      product.setQuantity(INVENTORY_QUANTITY);
      ++id;
      LineItem purchaseLineItem = new LineItem();
      purchaseLineItem.setProduct(product);
      purchaseLineItem.setQuantity(PURCHASE_QUANTITY);
      purchasesList.add(purchaseLineItem);
    }

    testPurchase.setProducts(purchasesList);
    testPurchase.setBillingAddress(testBillingAddress);
    testPurchase.setDeliveryAddress(testDeliveryAddress);
    testPurchase.setCreditCard(testCreditCard);
    testPurchase.setPromoCode(promoCode);
  }

  @Test
  public void savePurchaseReturnsPurchaseForValidInfo() {

    Purchase expected = testPurchase;

    Purchase actual = purchaseServiceImpl.savePurchase(testPurchase);
    assertEquals(expected, actual);
  }

  @Test
  public void savePurchaseIgnoresInvalidPromoCodeAndSaves() {
    //simulate promocode attached to purchase is not a valid title
    when(promotionalCodeService.getPromotionalCodeByTitle(anyString())).thenReturn(null);
    assertNotNull(testPurchase.getPromoCode());
    purchaseServiceImpl.savePurchase(testPurchase);
    assertNull(testPurchase.getPromoCode());
  }

  @Test
  public void savePurchaseSavesValidPromoCodeFromDataBase() {
    PromotionalCode fakePromo = new PromotionalCode();
    fakePromo.setTitle(promoCode.getTitle());
    fakePromo.setRate(BigDecimal.valueOf(30));
    fakePromo.setStartDate(promoCode.getStartDate());
    fakePromo.setEndDate(promoCode.getEndDate());
    fakePromo.setType(promoCode.getType());
    testPurchase.setPromoCode(fakePromo);
    Purchase actualPurchase = purchaseServiceImpl.savePurchase(testPurchase);

    assertEquals(BigDecimal.valueOf(TEST_CODE_RATE), actualPurchase.getPromoCode().getRate());
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
  public void findByBillingAddressEmailCallsPurchaseService() {

    List<Purchase> actual = purchaseServiceImpl.findByBillingAddressEmail(testEmail);
    assertEquals(testPurchases, actual);
  }

  @Test(expected = ServerError.class)
  public void findByBillingAddressEmailCatchesDataAccessException() {
    doThrow(new DataAccessException("Test exception") {
    }).when(purchaseRepository).findByBillingAddressEmail(testEmail);

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
  public void lineItemRepositoryErrorThrowsServerError() {
    doThrow(new DataAccessException("Test exception") {
    }).when(lineItemRepository).save(any(LineItem.class));
    Purchase savedPurchase = purchaseServiceImpl.savePurchase(testPurchase);
    fail(); //this should never run
  }

  @Test(expected = ServerError.class)
  public void savePurchaseThrowsServerError() {
    doThrow(new DataAccessException("Test exception") {
    }).when(purchaseRepository).save(testPurchase);
    Purchase copy = purchaseServiceImpl.savePurchase(testPurchase);
    fail(); //this should never run
  }

  @Test
  public void purchaseCalcLineItemTotalSingleItemTest() {
    final double PRICE = 1.00;
    final int QUANTITY = 49;
    DeliveryAddress delivery = new DeliveryAddress();
    delivery.setDeliveryState(StateEnum.WA.fullName);
    Set<LineItem> lineItems = new HashSet<>();
    Product product1 = new Product();
    product1.setPrice(PRICE);
    LineItem line1 = new LineItem();
    line1.setProduct(product1);
    line1.setQuantity(QUANTITY);
    lineItems.add(line1);
    Purchase purchase = new Purchase();
    purchase.setProducts(lineItems);
    purchase.setDeliveryAddress(delivery);

    assertEquals(PRICE * QUANTITY, purchase.calcLineItemTotal(), .001);
    assertTrue(purchase.applyShippingCharge());

  }

  @Test
  public void purchaseCalcLineItemTotalMultipleItemTest() {
    final double PRICE = 1.01;
    final int QUANTITY = 25;
    Set<LineItem> lineItems = new HashSet<>();
    Product product1 = new Product();
    Product product2 = new Product();
    product1.setPrice(PRICE);
    product2.setPrice(PRICE);
    product1.setBrand("brand1");
    product2.setBrand("different brand");
    LineItem line1 = new LineItem();
    LineItem line2 = new LineItem();

    line1.setProduct(product1);
    line1.setQuantity(QUANTITY);
    line2.setProduct(product2);
    line2.setQuantity(QUANTITY);

    DeliveryAddress delivery = new DeliveryAddress();
    delivery.setDeliveryState(StateEnum.RI.fullName);

    lineItems.add(line1);
    lineItems.add(line2);
    Purchase purchase = new Purchase();
    purchase.setProducts(lineItems);
    purchase.setDeliveryAddress(delivery);

    assertEquals(PRICE * QUANTITY * lineItems.size(), purchase.calcLineItemTotal(), .001);
    Assertions.assertFalse(purchase.applyShippingCharge());

  }

  @Test(expected = UnprocessableContent.class)
  public void savePurchaseThrowsUnprocessableContentForNotEnoughInventory() {
    int purchaseQuantity = INVENTORY_QUANTITY + PURCHASE_QUANTITY;
    testPurchase.getProducts().iterator().next().setQuantity(purchaseQuantity);
    purchaseServiceImpl.savePurchase(testPurchase);
    fail();//shouldn't run
  }

  @Test(expected = MultipleUnprocessableContent.class)
  public void savePurchaseThrowsMultipleUnprocessableContentForNotEnoughInventoryAndInactiveProduct() {
    int purchaseQuantity = INVENTORY_QUANTITY + PURCHASE_QUANTITY;
    testProducts.get(1).setActive(false);
    Iterator<LineItem> lines = testPurchase.getProducts().iterator();
    for (int count = 0; count < testPurchase.getProducts().size(); count++) {
      LineItem line = lines.next();
      if (count++ == 0) {
        line.setQuantity(purchaseQuantity);
      }
    }
    purchaseServiceImpl.savePurchase(testPurchase);
    fail();//shouldn't run
  }

  @Test
  public void purchaseApplyShippingChargeAlwaysTrueForAlaska() {
    final double PRICE = 1.00;
    final int QUANTITY = 100;
    DeliveryAddress deliveryAddress = new DeliveryAddress();
    deliveryAddress.setDeliveryState(StateEnum.AK.fullName);
    Set<LineItem> lineItems = new HashSet<>();
    Product product1 = new Product();
    product1.setPrice(PRICE);
    LineItem line1 = new LineItem();
    line1.setProduct(product1);
    line1.setQuantity(QUANTITY);
    lineItems.add(line1);
    Purchase purchase = new Purchase();
    purchase.setProducts(lineItems);
    purchase.setDeliveryAddress(deliveryAddress);

    assertTrue(purchase.applyShippingCharge());

  }

  @Test
  public void purchaseApplyShippingChargeAlwaysTrueForHawaii() {
    final double PRICE = 1.00;
    final int QUANTITY = 100;
    DeliveryAddress deliveryAddress = new DeliveryAddress();
    deliveryAddress.setDeliveryState(StateEnum.HI.fullName);
    Set<LineItem> lineItems = new HashSet<>();
    Product product1 = new Product();
    product1.setPrice(PRICE);
    LineItem line1 = new LineItem();
    line1.setProduct(product1);
    line1.setQuantity(QUANTITY);
    lineItems.add(line1);
    Purchase purchase = new Purchase();
    purchase.setProducts(lineItems);
    purchase.setDeliveryAddress(deliveryAddress);

    assertTrue(purchase.applyShippingCharge());

  }

}
