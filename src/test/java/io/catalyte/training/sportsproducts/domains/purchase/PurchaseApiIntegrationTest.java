package io.catalyte.training.sportsproducts.domains.purchase;

import static io.catalyte.training.sportsproducts.constants.Paths.PURCHASES_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PurchaseApiIntegrationTest {
  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;
  private List<BillingAddress> testAddresses;
  private String[] emails = {"email@address.com", "email@address.net", "email@address.edu"};
  private Map<String, Integer> purchaseCounts;
  @Resource
  public PurchaseRepository purchaseRepository;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

    //assemble some test objects
    testAddresses = new ArrayList<>();
    purchaseCounts = new HashMap<>();
    for(String email: emails){
      BillingAddress tempAddress = new BillingAddress();
      tempAddress.setEmail(email);
      testAddresses.add(tempAddress);
      Purchase tempPurchase = new Purchase();
      tempPurchase.setBillingAddress(tempAddress);
      purchaseRepository.save(tempPurchase);
      purchaseCounts.put(email, 1);
    }

    //add a purchase so 1 email returns a list of more than 1 purchase
    Purchase secondPurchase = new Purchase();
    BillingAddress secondEmail = new BillingAddress();
    secondEmail.setEmail(emails[0]);
    secondPurchase.setBillingAddress(secondEmail);
    purchaseRepository.save(secondPurchase);
    purchaseCounts.replace(emails[0], purchaseCounts.get(emails[0]) + 1);

  }

  @After
  public void tearDown(){
    for (String email: emails){
      List<Purchase> purchases = purchaseRepository.findByBillingAddressEmail(email);
      for (Purchase p: purchases){
        purchaseRepository.delete(p);
      }
    }
  }

  @Test
  public void dataBaseFixtureTest() throws Exception {
    List<Purchase> testPurchases = purchaseRepository.findAll();
    assertTrue(testPurchases.size() > 0);
  }


  @Test
  public void findPurchasesByEmailReturnsEmailList() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

    for (String email: emails) {
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
        mapper.readValue(purchasesJson, new TypeReference<List<Purchase>>() {}).size();
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
