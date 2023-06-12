package io.catalyte.training.movierentals.domains.movie;

import io.catalyte.training.movierentals.exceptions.ResourceNotFound;
import io.catalyte.training.movierentals.exceptions.ServerError;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

/**
 * This class provides the implementation for the ProductService interface.
 */
@Service
public class MovieServiceImpl implements MovieService {

  private final Logger logger = LogManager.getLogger(MovieServiceImpl.class);

  MovieRepository movieRepository;

  @Autowired
  public MovieServiceImpl(MovieRepository movieRepository) {
    this.movieRepository = movieRepository;
  }

  /**
   * Retrieves all products from the database, optionally making use of an example if it is passed.
   *
   * @param movie - an example product to use for querying
   * @return - a list of products matching the example, or all products if no example was passed
   */
  public List<Movie> getMovies(Movie movie) {
    try {
      return movieRepository.findAll(Example.of(movie));
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  /**
   * Retrieves the product with the provided id from the database.
   *
   * @param id - the id of the product to retrieve
   * @return - the product
   */
  public Movie getMovieById(Long id) {
    Movie movie;

    try {
      movie = movieRepository.findById(id).orElse(null);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }

    if (movie != null) {
      return movie;
    } else {
      logger.info("Get by id failed, it does not exist in the database: " + id);
      throw new ResourceNotFound("Get by id failed, it does not exist in the database: " + id);
    }
  }


  /**
   * Adds a movie to the database
   *
   * @param movie - product object
   * @return list of movie objects that are added to database
   */
  public Movie saveMovie(Movie movie) {
//    List<String> productErrors = getProductErrors(movie);

//    if (!productErrors.isEmpty()) {
//      throw new BadRequest(String.join("\n", productErrors));
//    }

    try {
      return movieRepository.save(movie);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  public Movie updateMovie(Long id, Movie movie){
    return movie;
  }

  public void deleteMovie(Long id){

  }



  //TODO: Use these helper methods as basis for validation of save/update product
  /**
   * Helper method that reads a product and validateds it's properties
   *
   * @param product product to be validated
   * @return a list of errrors
   */
//  public List<String> getProductErrors(Product product) {
//    List<String> errors = new ArrayList<>();
//    Boolean priceIsValid = validateProductPrice(product);
//    List<String> emptyFields = getFieldsEmptyOrNull(product).get("emptyFields");
//    List<String> nullFields = getFieldsEmptyOrNull(product).get("nullFields");
//    Boolean quantityIsValid = validateProductQuantity(product);
//
//    if (!nullFields.isEmpty()) {
//      errors.add(StringConstants.PRODUCT_FIELDS_NULL(nullFields));
//    }
//
//    if (!emptyFields.isEmpty()) {
//      errors.add(StringConstants.PRODUCT_FIELDS_EMPTY(emptyFields));
//    }
//
//    if (!priceIsValid) {
//      errors.add(StringConstants.PRODUCT_PRICE_INVALID);
//    }
//
//    if (!quantityIsValid) {
//      errors.add(StringConstants.PRODUCT_QUANTITY_INVALID);
//    }
//
//    return errors;
//  }

  /**
   * Checks price is a double value greater than zero and does not have more than 2 digits after the
   * decimal
   * <p>
   * Because price is stored as a double, regardless of input the product will always have 1 digit
   * after the decimal even if input as an integer, or with 2 zeros after decimal
   *
   * @param movie product to be validated
   * @return boolean if product price is valid
   */
//  public Boolean validateProductPrice(Movie movie) {
//    if (movie.getDailyRentalCost() != null) {
//      //Split price by the decimal
//      String[] priceString = String.valueOf(movie.getDailyRentalCost()).split("\\.");
//      Boolean priceMoreThan2Decimals = priceString[1].length() > 2;
//      Boolean priceLessThanZero = movie.getDailyRentalCost() > 0;
//      return priceLessThanZero || priceMoreThan2Decimals;
//    }
//    return false;
//  }
//
//  /**
//   * Validates a products quantity is not a negative number
//   *
//   * @param product product to be validated
//   * @return boolean if product has valid quantity
//   */
//  public Boolean validateProductQuantity(Product product) {
//    if (product.getQuantity() != null) {
//      return product.getQuantity() >= 0;
//    }
//    return false;
//  }

//  /**
//   * Reads a products fields and checks for fields that are empty or null
//   *
//   * @param product product to be validated
//   * @return A Hashmap {"emptyFields": List of empty fields, "nullFields": list of null fields}
//   */
//  public HashMap<String, List<String>> getFieldsEmptyOrNull(Product product) {
//    List<Field> productFields = Arrays.asList(Product.class.getDeclaredFields());
//    List<String> productFieldNames = new ArrayList<>();
//    List<String> emptyFields = new ArrayList<>();
//    List<String> nullFields = new ArrayList<>();
//    HashMap<String, List<String>> results = new HashMap<>();
//    //Get product field names
//    productFields.forEach((field -> productFieldNames.add(field.getName())));
//    //Remove id as product will not have an id before it is saved
//    productFieldNames.remove("id");
//    //Convert product to a HashMap
//    ObjectMapper mapper = new ObjectMapper();
//    Map productMap = mapper.convertValue(product, HashMap.class);
//    //Loop through each fieldName to retrieve each product mapping value of the field
//    productFieldNames.forEach((field) -> {
//      //Check if the value for the product's field is null or empty and place in the corresponding list
//      if (productMap.get(field) == null && field != "reviews") {
//        nullFields.add(field);
//      } else if (field != "reviews" && productMap.get(field).toString().trim() == "") {
//        emptyFields.add(field);
//      }
//    });
//    //place each list in the results
//    results.put("emptyFields", emptyFields);
//    results.put("nullFields", nullFields);
//    return results;
//  }


}