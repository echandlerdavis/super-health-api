package io.catalyte.training.sportsproducts.domains.purchase;

import static io.catalyte.training.sportsproducts.constants.Paths.PURCHASES_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashSet;
import java.util.Set;
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

    @Autowired
    private ProductRepository productRepository;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
        setTestPurchase();
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
}
