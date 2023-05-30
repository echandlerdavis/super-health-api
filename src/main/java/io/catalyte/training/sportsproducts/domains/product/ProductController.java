package io.catalyte.training.sportsproducts.domains.product;

import static io.catalyte.training.sportsproducts.constants.Paths.PRODUCTS_PATH;

import java.util.HashMap;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * The ProductController exposes endpoints for product related actions.
 */
@RestController
@RequestMapping(value = PRODUCTS_PATH)
public class ProductController {

  Logger logger = LogManager.getLogger(ProductController.class);

  @Autowired
  private ProductService productService;

  /**
   * Handles a GET request to /products - returns all products in the database.
   *
   * @param product - optional product example to be passed
   * @return all products in the database.
   */
  @GetMapping
  public ResponseEntity<List<Product>> getProducts(Product product) {
    logger.info("Request received for getProducts");

    return new ResponseEntity<>(productService.getProducts(product), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /products/{id}- returns a single product based on an id defined in the
   * path variable
   *
   * @param - path variable id
   * @return a single product from the product's id.
   */
  @GetMapping(value = "/{id}")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    logger.info("Request received for getProductsById: " + id);

    return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /types- returns all unique values of type
   *
   * @return unique type values
   */
  @GetMapping(value = "/types")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctType() {
    logger.info("Request received for getDistinctTypes");
    return new ResponseEntity<>(productService.getDistinctTypes(), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /categories- returns all unique values of category
   *
   * @return unique category values
   */
  @GetMapping(value = "/categories")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctCategory() {
    logger.info("Request received for getDistinctCategory");
    return new ResponseEntity<>(productService.getDistinctCategories(), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /categories- returns all unique values of category
   *
   * @return unique category values
   */
  @GetMapping(value = "/brands")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctBrands() {
    logger.info("Request received for getDistinctBrands");
    return new ResponseEntity<>(productService.getDistinctBrands(), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /categories- returns all unique values of category
   *
   * @return unique category values
   */
  @GetMapping(value = "/materials")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctMaterials() {
    logger.info("Request received for getDistinctMaterials");
    return new ResponseEntity<>(productService.getDistinctMaterials(), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /demographics- returns all unique values of category
   *
   * @return unique category values
   */
  @GetMapping(value = "/demographics")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctDemographics() {
    logger.info("Request received for getDistinctDemographics");
    return new ResponseEntity<>(productService.getDistinctDemographics(), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /primarycolors- returns all unique values of category
   *
   * @return unique category values
   */
  @GetMapping(value = "/primarycolors")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctPrimaryColors() {
    logger.info("Request received for getDistinctPrimaryColors");
    return new ResponseEntity<>(productService.getDistinctPrimaryColors(), HttpStatus.OK);
  }

  /**
   * Handles a GET request to /secondarycolors- returns all unique values of category
   *
   * @return unique category values
   */
  @GetMapping(value = "/secondarycolors")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getDistinctSecondaryColors() {
    logger.info("Request received for getDistinctSecondaryColors");
    return new ResponseEntity<>(productService.getDistinctSecondaryColors(), HttpStatus.OK);
  }

  /**
   * Handles a POST request to /products. This creates a new product object that gets saved to the
   * database.
   *
   * @param product - product object
   * @return product(s) added to database
   */
  @PostMapping
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<Product> postProduct(@RequestBody Product product) {
    logger.info("Request recieved for postProduct");
    return new ResponseEntity<>(productService.saveProduct(product), HttpStatus.CREATED);
  }

  /**
   * Handles a GET request to /products/filters. This retrieves all products in the database with
   * applied query. Methods have been implemented to handle the following query params: brand,
   * category, priceMin, priceMax, material, primaryColor, and demographic For multiple values
   * passed into a query param the URL-Encoded character for |, "%7C" should be placed in between in
   * url request Example: '/filter?priceMin=0&priceMax=50&brand=nike%7Cchampion' requests to filter
   * by brands Nike or Champion and price between 0 and 50
   *
   * @param filters - the filters to be read from the request parameters
   * @return product(s) found matching the given filters
   */
  @GetMapping(value = "/filter")
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<List> getProductsByFilters(@RequestParam HashMap<String, String> filters) {
    logger.info("Request received for getProductsByFilters: " + filters.toString());
    return new ResponseEntity<>(productService.getProductsByFilters(filters), HttpStatus.OK);
  }
}
