package io.catalyte.training.movierentals.domains.review;

import static io.catalyte.training.movierentals.constants.Paths.PRODUCTS_PATH;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.catalyte.training.movierentals.data.ProductFactory;
import io.catalyte.training.movierentals.domains.movie.Product;
import io.catalyte.training.movierentals.domains.movie.MovieRepository;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReviewApiTest {

  @Autowired
  ReviewRepository reviewRepository;
  @Autowired
  MovieRepository movieRepository;
  ProductFactory productFactory = new ProductFactory();
  Product testProduct = productFactory.createRandomProduct();
  Review testReview1 = productFactory.createRandomReview(testProduct, 1);
  Review testReview2 = productFactory.createRandomReview(testProduct, 2);
  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    setTestReviews();
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  private void setTestReviews() {
    movieRepository.save(testProduct);
    testProduct.setReviews(Arrays.asList(testReview1, testReview2));
    reviewRepository.saveAll(Arrays.asList(testReview1, testReview2));
  }

  @Test
  public void getReviewsByProductIdReturns200() throws Exception {
    mockMvc.perform(get(PRODUCTS_PATH + "/1/reviews"))
        .andExpect(status().isOk());
  }
}
