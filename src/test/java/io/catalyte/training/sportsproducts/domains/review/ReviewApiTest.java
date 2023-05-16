package io.catalyte.training.sportsproducts.domains.review;

import io.catalyte.training.sportsproducts.data.ProductFactory;
import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductRepository;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static io.catalyte.training.sportsproducts.constants.Paths.PRODUCTS_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class ReviewApiTest {
  @Autowired
  private WebApplicationContext wac;

  private MockMvc mockMvc;

  @Autowired
  ReviewRepository reviewRepository;

  @Autowired
  ProductRepository productRepository;

  ProductFactory productFactory = new ProductFactory();
  Product testProduct = productFactory.createRandomProduct();

  Review testReview1 = productFactory.createRandomReview(testProduct, 1);
  Review testReview2 = productFactory.createRandomReview(testProduct, 2);

  @Before
  public void setUp(){
    setTestReviews();
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  private void setTestReviews(){
    productRepository.save(testProduct);
    testProduct.setReviews(Arrays.asList(testReview1, testReview2));
    reviewRepository.saveAll(Arrays.asList(testReview1, testReview2));
  }

  @Test
  public void getReviewsByProductIdReturns200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/1/reviews"))
        .andExpect(status().isOk());
  }
}
