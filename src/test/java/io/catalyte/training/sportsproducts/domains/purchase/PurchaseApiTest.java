package io.catalyte.training.sportsproducts.domains.purchase;

import static io.catalyte.training.sportsproducts.constants.Paths.PURCHASES_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.sportsproducts.data.ProductFactory;
import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductRepository;
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
public class PurchaseApiTest {

  private final int INVENTORY_QUANTITY = 100;
  private final int PURCHASE_QUANTITY = 1;
  private final Purchase testPurchase = new Purchase();
  private final CreditCard testCreditCard = new CreditCard("1234567890123456", "111", "04/30",
      "Visa");
  private final String[] emails = {"email@address.com", "email@address.net", "email@address.edu"};
  private final ProductFactory productFactory = new ProductFactory();
  @Autowired
  public PurchaseRepository purchaseRepository;

  @Autowired
  public ProductRepository productRepository;
  //TODO: test for getting state shipping cost info
  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;
  private List<BillingAddress> testAddresses;
  private Map<String, Integer> purchaseCounts;
  private List<Product> testProducts;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    setTestPurchase();
    saveTestPurchasesToRepositoryWithDifferentEmails();
  }

  /**
   * Helper method initializes a test purchase with billing address, delivery address, credit card
   * info, and product with id of 1 to be sent in POST method
   */
  private void setTestPurchase() {

    BillingAddress testBillingAddress = new BillingAddress(
        "123 No Name Street",
        null,
        "No City",
        "Virginia",
        12345,
        "bob@ross.com",
        "800-555-5555");

    DeliveryAddress testDeliveryAddress = new DeliveryAddress(
        "first",
        "last",
        "123 No Name Street",
        null,
        "No City",
        "Virginia",
        12345);

    // Generate random Products and save to repository
    testProducts = productFactory.generateRandomProducts(3);

    // Get List of test products to add to purchase
    Set<LineItem> purchasesList = new HashSet<>();

    testProducts.forEach(product -> {
      product.setActive(true);
      product.setQuantity(INVENTORY_QUANTITY);
      Product savedProduct = productRepository.save(product);
      LineItem purchaseLineItem = new LineItem();
      purchaseLineItem.setProduct(savedProduct);
      purchaseLineItem.setQuantity(PURCHASE_QUANTITY);
      purchasesList.add(purchaseLineItem);
    });

    testPurchase.setProducts(purchasesList);
    testPurchase.setBillingAddress(testBillingAddress);
    testPurchase.setDeliveryAddress(testDeliveryAddress);
    testPurchase.setCreditCard(testCreditCard);
    testPurchase.setPromoCode(null);

    testAddresses = new ArrayList<>();
    purchaseCounts = new HashMap<>();
    for (String email : emails) {
      Purchase tempPurchase = new Purchase();
      BillingAddress tempAddress = testBillingAddress;
      tempAddress.setEmail(email);
      testAddresses.add(tempAddress);
      tempPurchase.setBillingAddress(tempAddress);
      tempPurchase.setDeliveryAddress(testDeliveryAddress);
      tempPurchase.setCreditCard(testCreditCard);
      tempPurchase.setProducts(purchasesList);
      purchaseRepository.save(tempPurchase);
      purchaseCounts.put(email, 1);
    }

  }


  /**
   * Remove purchases that were added in setup.
   */
  @After
  public void tearDown() {
    //delete purchases
    for (String email : emails) {
      List<Purchase> purchases = purchaseRepository.findByBillingAddressEmail(email);
      for (Purchase p : purchases) {
        purchaseRepository.delete(p);
      }
    }
    //reset inventory quantities
    for (Product p : testProducts) {
      p.setQuantity(INVENTORY_QUANTITY);
      productRepository.save(p);
    }

  }

  private void saveTestPurchasesToRepositoryWithDifferentEmails() {
    //assemble some test objects

    //add a purchase so 1 email returns a list of more than 1 purchase
    Purchase secondPurchase = new Purchase();
    BillingAddress secondEmail = new BillingAddress();
    secondEmail.setEmail(emails[0]);
    secondPurchase.setBillingAddress(secondEmail);
    purchaseRepository.save(secondPurchase);
    purchaseCounts.replace(emails[0], purchaseCounts.get(emails[0]) + 1);
  }

  @Test
  public void savePurchasesWithoutCreditCardReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with credit card number less than 16 digits
    testPurchase.setCreditCard(null);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchasesWithoutCardNumberReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with null credit card number
    testCreditCard.setCardNumber(null);
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchasesWithoutCVVReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with credit card number less than 16 digits
    testCreditCard.setCvv(null);
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchasesWithoutExpirationReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with credit card number less than 16 digits
    testCreditCard.setExpiration(null);
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchasesWithCCNumberLessThan16DigitsReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with credit card number less than 16 digits
    testCreditCard.setCardNumber("123456");
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchasesWithCCNumberGreaterThan16DigitsReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with credit card number less than 16 digits
    testCreditCard.setCardNumber("12345678901234567");
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchasesWithCCNumberWithLettersReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with credit card number less than 16 digits
    testCreditCard.setCardNumber("123456abcde12345");
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }


  @Test
  public void savePurchaseWithCVVLessThan3Digits() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with credit card CVV less than 3 digits
    testCreditCard.setCvv("01");
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchaseWithCVVWithLettersReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set test purchase with credit card CVV less than 3 digits
    testCreditCard.setCvv("01a");
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchaseWithCardWithExpirationDateNotCorrectFormatReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // set test purchase with card that has expiration not in format MM/YY
    testCreditCard.setExpiration("04/2027");
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savePurchaseWithExpiredCardReturns400() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // set test purchase with expired credit card
    testCreditCard.setExpiration("04/20");
    testPurchase.setCreditCard(testCreditCard);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void dataBaseFixtureTest() throws Exception {
    saveTestPurchasesToRepositoryWithDifferentEmails();
    List<Purchase> testPurchases = purchaseRepository.findAll();
    assertTrue(testPurchases.size() > 0);
  }

  @Test
  public void findPurchasesByEmailReturnsEmailList() throws Exception {

    ObjectMapper mapper = new ObjectMapper();

    for (String email : emails) {
      MockHttpServletResponse response = mockMvc.perform(get(PURCHASES_PATH + "/" + email))
          .andReturn().getResponse();
      List<Purchase> purchases = mapper.readValue(response.getContentAsString(),
          new TypeReference<List<Purchase>>() {
          });
      assertEquals(purchaseCounts.get(email), Integer.valueOf(purchases.size()));
    }

    String purchasesJson =
        mockMvc.perform(
                get(PURCHASES_PATH + "/" + emails[0]))
            .andReturn()
            .getResponse()
            .getContentAsString();
    Integer numPurchases =
        mapper.readValue(purchasesJson, new TypeReference<List<Purchase>>() {
        }).size();
    assertEquals(Integer.valueOf(2), numPurchases);
  }

  @Test
  public void findPurchasesByEmailEmailNotFoundReturnsEmptyList() throws Exception {
    String purchases = mockMvc.perform(get(PURCHASES_PATH + "/not@anEmail.com"))
        .andReturn().getResponse().getContentAsString();
    assertEquals("[]", purchases);
  }

  @Test
  public void findPurchasesByEmailEmailNotFoundReturnsOk() throws Exception {
    mockMvc.perform(get(PURCHASES_PATH + "/not@anEmail.com"))
        .andExpect(status().isOk());
  }

  @Test
  public void getAllPurchasesReturns404() throws Exception {
    mockMvc.perform(get(PURCHASES_PATH))
        .andExpect(status().is(404));
  }

  @Test
  public void savingPurchaseWithAllInactiveProductsThrowsError() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set all test products to inactive
    testProducts.forEach(product -> product.setActive(false));
    productRepository.saveAll(testProducts);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void savingPurchaseWithOneInactiveProductThrowsError() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set one test product to be inactive
    testProducts.get(2).setActive(false);
    productRepository.save(testProducts.get(2));

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void savingPurchaseWithoutAnyProductsThrowsError() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set all test products to be null
    testPurchase.setProducts(null);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void savingPurchaseIfEveryProductActiveStatusIsNullThrowsError() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set all test products active status to be null
    testProducts.forEach(product -> product.setActive(null));
    productRepository.saveAll(testProducts);

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void savingPurchaseIfOneProductActiveStatusIsNullThrowsError() throws Exception {
    // object mapper for creating a json string
    ObjectMapper mapper = new ObjectMapper();

    // Set one product to null active status
    testProducts.get(0).setActive(null);
    productRepository.save(testProducts.get(0));

    // Convert purchase to json string
    String JsonString = mapper.writeValueAsString(testPurchase);

    mockMvc.perform(post(PURCHASES_PATH)
            .content(JsonString)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isUnprocessableEntity());
  }

  @Test
  public void postPurchasesReturnsPurchaseObject() throws Exception {
    //This test fails when run with coverage
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response =
        mockMvc.perform(
                post(PURCHASES_PATH)
                    .contentType("application/json")
                    .content(mapper.writeValueAsString(testPurchase)))
            .andReturn().getResponse();

    Purchase returnedPurchase = mapper.readValue(response.getContentAsString(), Purchase.class);

    assertTrue(twoPurchasesEqualExceptId(testPurchase, returnedPurchase));
    assertNotNull(returnedPurchase.getId());
  }

  public boolean twoPurchasesEqualExceptId(Purchase p1, Purchase p2) {
    if (p1 == null && p2 != null) {
      return false;
    }
    if (p1 != null && p2 == null) {
      return false;
    }
    if (!p1.getDeliveryAddress().equals(p2.getDeliveryAddress())) {
      return false;
    }
    if (!p1.getBillingAddress().equals(p2.getBillingAddress())) {
      return false;
    }
    return p1.getCreditCard().equals(p2.getCreditCard());
  }

  @Test
  public void updateInventoryTest() throws Exception {
    //This test fails when run with coverage
    int expectedEndingInventory = INVENTORY_QUANTITY - PURCHASE_QUANTITY;
    ObjectMapper mapper = new ObjectMapper();
    mockMvc.perform(
            post(PURCHASES_PATH)
                .contentType("application/json")
                .content(mapper.writeValueAsString(testPurchase)))
        .andReturn().getResponse();

    for (Product p : testProducts) {
      Product updatedProduct = productRepository.findById(p.getId()).get();
      assertEquals(Long.valueOf(expectedEndingInventory),
          Long.valueOf(updatedProduct.getQuantity()));
    }
  }

  @Test
  public void shippingChargeIs0WhenPurchaseAbove50() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    //Update the prices on the purchase items
    double price = 16.67;
    int purchaseQty = 1;
    for (Product p : testProducts) {
      p.setPrice(price);
      productRepository.save(p);
    }
    //update the purchase quantities
    for (LineItem l : testPurchase.getProducts()) {
      l.setQuantity(purchaseQty);
    }
    //set state
    testPurchase.getDeliveryAddress().setDeliveryState(StateEnum.ID.fullName);
    //save purchase
    MockHttpServletResponse result = mockMvc.perform(
            post(PURCHASES_PATH)
                .contentType("application/json")
                .content(mapper.writeValueAsString(testPurchase)))
        .andReturn().getResponse();
    String json = result.getContentAsString();
    Purchase returnedPurchase = mapper.readValue(json, Purchase.class);
    //assertions
    assertFalse(returnedPurchase.applyShippingCharge());
    assertEquals(0.00, returnedPurchase.getShippingCharge(), .001);

  }

  @Test
  public void shippingChargeIs5WhenPurchaseBelow50AndDeliverToLower48() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    StateEnum state = StateEnum.AL;
    //Update the prices on the purchase items
    double price = 16.66;
    int purchaseQty = 1;
    for (Product p : testProducts) {
      p.setPrice(price);
      productRepository.save(p);
    }
    //update the purchase quantities
    for (LineItem l : testPurchase.getProducts()) {
      l.setQuantity(purchaseQty);
    }
    //set state
    testPurchase.getDeliveryAddress().setDeliveryState(state.fullName);
    //save purchase
    MockHttpServletResponse result = mockMvc.perform(
            post(PURCHASES_PATH)
                .contentType("application/json")
                .content(mapper.writeValueAsString(testPurchase)))
        .andReturn().getResponse();
    String json = result.getContentAsString();
    Purchase returnedPurchase = mapper.readValue(json, Purchase.class);
    //assertions
    assertTrue(returnedPurchase.applyShippingCharge());
    assertEquals(state.shippingCost, returnedPurchase.getShippingCharge(), .001);

  }

  @Test
  public void shippingChargeIs10WhenShippingToAlaska() throws Exception {
    StateEnum state = StateEnum.AK;
    ObjectMapper mapper = new ObjectMapper();
    //Update the prices on the purchase items
    double price = 100.00;
    int purchaseQty = 1;
    for (Product p : testProducts) {
      p.setPrice(price);
      productRepository.save(p);
    }
    //update the purchase quantities
    for (LineItem l : testPurchase.getProducts()) {
      l.setQuantity(purchaseQty);
    }
    //set state
    testPurchase.getDeliveryAddress().setDeliveryState(state.fullName);
    //save purchase
    MockHttpServletResponse result = mockMvc.perform(
            post(PURCHASES_PATH)
                .contentType("application/json")
                .content(mapper.writeValueAsString(testPurchase)))
        .andReturn().getResponse();
    String json = result.getContentAsString();
    Purchase returnedPurchase = mapper.readValue(json, Purchase.class);
    //assertions
    assertTrue(returnedPurchase.applyShippingCharge());
    assertEquals(state.shippingCost, returnedPurchase.getShippingCharge(), .001);

  }

  @Test
  public void shippingChargeIs10WhenShippingToHawaii() throws Exception {
    StateEnum state = StateEnum.HI;
    ObjectMapper mapper = new ObjectMapper();
    //Update the prices on the purchase items
    double price = 100.00;
    int purchaseQty = 1;
    for (Product p : testProducts) {
      p.setPrice(price);
      productRepository.save(p);
    }
    //update the purchase quantities
    for (LineItem l : testPurchase.getProducts()) {
      l.setQuantity(purchaseQty);
    }
    //set state
    testPurchase.getDeliveryAddress().setDeliveryState(state.fullName);
    //save purchase
    MockHttpServletResponse result = mockMvc.perform(
            post(PURCHASES_PATH)
                .contentType("application/json")
                .content(mapper.writeValueAsString(testPurchase)))
        .andReturn().getResponse();
    String json = result.getContentAsString();
    Purchase returnedPurchase = mapper.readValue(json, Purchase.class);
    //assertions
    assertTrue(returnedPurchase.applyShippingCharge());
    assertEquals(state.shippingCost, returnedPurchase.getShippingCharge(), .001);

  }
}
