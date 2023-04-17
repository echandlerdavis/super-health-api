package io.catalyte.training.sportsproducts.domains.product;
import static io.catalyte.training.sportsproducts.constants.Paths.PRODUCTS_PATH;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


/**
 * The ProductController exposes endpoints for product related actions.
 */
@RestController
@RequestMapping(value = PRODUCTS_PATH)
public class ProductController {

  Logger logger = LogManager.getLogger(ProductController.class);

  @Autowired
  private ProductService productService;

  @GetMapping
  public ResponseEntity<List<Product>> getProducts(Product product) {
    logger.info("Request received for getProducts");

    return new ResponseEntity<>(productService.getProducts(product), HttpStatus.OK);
  }

  /**
   * GET request - returns a single product based on an id defined in the path variable
   * */
  @GetMapping(value = "/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    logger.info("Request received for getProductsById: " + id);

    return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
  }

  /**
   *
   * GET request - returns all unique values of type
   */
  @GetMapping(value = "/types")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctType(){
    logger.info("Request received for getDistinctTypes");
    return new ResponseEntity<>(productService.getDistinctTypes(), HttpStatus.OK);
  }



  /**
   *
   * GET request - returns all unique values of category
   */
  @GetMapping(value = "/categories")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctCategory(){
    logger.info("Request received for getDistinctCategory");
    return new ResponseEntity<>(productService.getDistinctCategories(), HttpStatus.OK);
  }

  /**
   *
   * Handles a POST request to /products. This creates a new product object that gets saved to the database.
   * @param products - list of product object(s)
   * @return product(s) added to database
   */
  @PostMapping
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> postProduct(@RequestBody List<Product> products){
    productService.addProducts(products);
    return new ResponseEntity<>(productService.addProducts(products), HttpStatus.CREATED);
  }

}
