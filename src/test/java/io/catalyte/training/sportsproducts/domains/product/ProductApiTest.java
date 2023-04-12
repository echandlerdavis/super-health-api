package io.catalyte.training.sportsproducts.domains.product;
import static io.catalyte.training.sportsproducts.constants.Paths.PRODUCTS_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductApiTest {

  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  private final String  testProducts =
          "[{\n" +
          "\"id\": 1001,\n" +
          "        \"brand\": null,\n" +
          "        \"imageSrc\": null,\n" +
          "        \"material\": null,\n" +
          "        \"price\": null,\n" +
          "        \"quantity\": null,\n" +
          "        \"name\": null,\n" +
          "        \"description\": null,\n" +
          "        \"demographic\": \"Kids\",\n" +
          "        \"category\": \"Casual\",\n" +
          "        \"type\": \"Short\",\n" +
          "        \"releaseDate\": null,\n" +
          "        \"primaryColorCode\": null,\n" +
          "        \"secondaryColorCode\": null,\n" +
          "        \"styleNumber\": \"sc97545\",\n" +
          "        \"globalProductCode\": \"po-7258282\",\n" +
          "        \"active\": null\n" +
          "    }," +
          "    {\n" +
          "        \"id\": 1002,\n" +
          "        \"brand\": null,\n" +
          "        \"imageSrc\": null,\n" +
          "        \"material\": null,\n" +
          "        \"price\": null,\n" +
          "        \"quantity\": null,\n" +
          "        \"name\": null,\n" +
          "        \"description\": null,\n" +
          "        \"demographic\": \"Kids\",\n" +
          "        \"category\": \"Running\",\n" +
          "        \"type\": \"Tall\",\n" +
          "        \"releaseDate\": null,\n" +
          "        \"primaryColorCode\": null,\n" +
          "        \"secondaryColorCode\": null,\n" +
          "        \"styleNumber\": \"sc97545\",\n" +
          "        \"globalProductCode\": \"po-7258282\",\n" +
          "        \"active\": null\n" +
          "    }," +
          "    {\n" +
          "        \"id\": 1003,\n" +
          "        \"brand\": null,\n" +
          "        \"imageSrc\": null,\n" +
          "        \"material\": null,\n" +
          "        \"price\": null,\n" +
          "        \"quantity\": null,\n" +
          "        \"name\": null,\n" +
          "        \"description\": null,\n" +
          "        \"demographic\": \"Kids\",\n" +
          "        \"category\": \"Hiking\",\n" +
          "        \"type\": \"Wide\",\n" +
          "        \"releaseDate\": null,\n" +
          "        \"primaryColorCode\": null,\n" +
          "        \"secondaryColorCode\": null,\n" +
          "        \"styleNumber\": \"sc97545\",\n" +
          "        \"globalProductCode\": \"po-7258282\",\n" +
          "        \"active\": null\n" +
          "    }]";


  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void getProductsReturns200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH))
        .andExpect(status().isOk());
  }

  @Test
  public void getProductByIdReturnsProductWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/1"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctTypesReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/types"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctCategoriesReturnsWith200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/categories"))
        .andExpect(status().isOk());
  }

  @Test
  public void getDistinctTypesReturnsAllAndOnlyUniqueTypes()throws Exception {

    //POST testProducts to database temporarily
    mockMvc.perform((post(PRODUCTS_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(testProducts)))
            .andExpect(status().isCreated());
    //GET categories and check if it is returning each unique type, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/types"))
            .andExpect(ResultMatcher.matchAll(jsonPath("$.length()", Matchers.is(3))));
  }

  @Test
  public void getDistinctCategoriesReturnsAllAndOnlyUniqueCategories()throws Exception {

    //POST testProducts to database temporarily
    mockMvc.perform((post(PRODUCTS_PATH)
            .contentType(MediaType.APPLICATION_JSON)
            .content(testProducts)))
            .andExpect(status().isCreated());
    //GET categories and check if it is returning each unique category, only once.
    mockMvc.perform(get(PRODUCTS_PATH + "/categories")).andExpect(ResultMatcher.matchAll(jsonPath("$.length()", Matchers.is(3))));
  }
}