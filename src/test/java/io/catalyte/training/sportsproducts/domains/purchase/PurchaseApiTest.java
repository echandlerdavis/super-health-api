package io.catalyte.training.sportsproducts.domains.purchase;

import static io.catalyte.training.sportsproducts.constants.Paths.PURCHASES_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductRepository;
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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PurchaseApiTest {
    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    private Purchase testPurchase = new Purchase();

    private CreditCard testCreditCard = new CreditCard("1234567890123456", "111", "04/30", "Visa");

    private List<BillingAddress> testAddresses;
    private String[] emails = {"email@address.com", "email@address.net", "email@address.edu"};
    private Map<String, Integer> purchaseCounts;

    @Autowired
    public PurchaseRepository purchaseRepository;

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        setTestPurchase();
        saveTestPurchasesToRepositoryWithDifferentEmails();
    }

    /**
     * Helper method initializes a test purchase with billing address, delivery address, credit card info, and product with id of 1
     * to be sent in POST method
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

        Product product = productRepository.findById(1L).orElse(null);
        LineItem productPurchase = new LineItem();
        productPurchase.setProduct(product);
        Set<LineItem> purchases = Stream.of(productPurchase)
                .collect(Collectors.toCollection(HashSet::new));

        testPurchase.setProducts(purchases);
        testPurchase.setBillingAddress(testBillingAddress);
        testPurchase.setDeliveryAddress(testDeliveryAddress);
        testPurchase.setCreditCard(testCreditCard);

        testAddresses = new ArrayList<>();
        purchaseCounts = new HashMap<>();
        for(String email: emails){
            Purchase tempPurchase = new Purchase();
            BillingAddress tempAddress = testBillingAddress;
            tempAddress.setEmail(email);
            testAddresses.add(tempAddress);
            tempPurchase.setBillingAddress(tempAddress);
            tempPurchase.setDeliveryAddress(testDeliveryAddress);
            tempPurchase.setCreditCard(testCreditCard);
            tempPurchase.setProducts(purchases);
            purchaseRepository.save(tempPurchase);
            purchaseCounts.put(email, 1);
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
    public void savePurchaseReturns201() throws Exception {

        // object mapper for creating a json string
        ObjectMapper mapper = new ObjectMapper();

        // Convert purchase to json string
        String JsonString = mapper.writeValueAsString(testPurchase);

        mockMvc.perform(post(PURCHASES_PATH)
                        .content(JsonString)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
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

        System.out.println("purchaseRepository.findAll() = " + purchaseRepository.findAll());
        ObjectMapper mapper = new ObjectMapper();

        for (String email : emails) {
            MockHttpServletResponse response = mockMvc.perform(get(PURCHASES_PATH + "/" + email))
                    .andReturn().getResponse();
            List<Purchase> purchases = mapper.readValue(response.getContentAsString(),
                    new TypeReference<List<Purchase>>() {
                    });
            assertEquals(Integer.valueOf(purchaseCounts.get(email)), Integer.valueOf(purchases.size()));
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
}
