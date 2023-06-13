package io.catalyte.training.movierentals.domains.purchase;

import io.catalyte.training.movierentals.constants.StringConstants;
import io.catalyte.training.movierentals.domains.movie.MovieService;
import io.catalyte.training.movierentals.domains.rental.Rental;
import io.catalyte.training.movierentals.domains.rental.RentalRepository;
import io.catalyte.training.movierentals.domains.rental.RentalService;
import io.catalyte.training.movierentals.exceptions.BadRequest;
import io.catalyte.training.movierentals.exceptions.MultipleUnprocessableContent;
import io.catalyte.training.movierentals.exceptions.ResourceNotFound;
import io.catalyte.training.movierentals.exceptions.ServerError;
import io.catalyte.training.movierentals.exceptions.UnprocessableContent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class RentalServiceImpl implements RentalService {

  private final Logger logger = LogManager.getLogger(RentalServiceImpl.class);

  RentalRepository rentalRepository;

  @Autowired
  public RentalServiceImpl(RentalRepository rentalRepository) {
    this.rentalRepository = rentalRepository;
  }

  /**
   * Retrieves all purchases from the database
   *
   * @return
   */
  public List<Rental> getRentals() {
    try {
      return rentalRepository.findAll();
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  /**
   * Search for single rental by rental id.
   *
   * @param id Long id
   * @return a single rental object.
   */
  public Rental getRentalById(Long id) {
    Rental rental;

    try{
      rental = rentalRepository.findById(id).orElse(null);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }

    if(rental !=null){
      return rental;
    } else {
      logger.info("Get by id failed, it does not exist in the database: " + id);
      throw new ResourceNotFound("Get by id failed, it does not exist in the database: " + id);
    }
  }

  /**
   * Persists a rental to the database
   *
   * @param newRental - the rental to persist
   * @return the persisted rental object
   */
  public Rental saveRental(Rental newRental) {
   //TODO: Validation to save.
//    TODO: fix it so the rentedMovie ids are not null. (Look at Purchase/Line Item relationship)
    try {
      return rentalRepository.save(newRental);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  public Rental updateRental(Long id, Rental updatedRental){
    Rental findRental = rentalRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFound("Cannot update a movie that does not exist."));

    //TODO: Validation for each.
    //TODO: Handle Rented Movies List - I think they need to be persisted individually/saved to their own repository
    try {
      findRental.setId(id);
      findRental.setRentalDate(updatedRental.getRentalDate());
      findRental.setRentedMovies(updatedRental.getRentedMovies());
      findRental.setRentalTotalCost(updatedRental.getRentalTotalCost());
      return rentalRepository.save(findRental);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  public void deleteRentalById(Long id){
    if(rentalRepository.findById(id) == null){
      throw new ResourceNotFound("You cannot delete a rental that doesn't exist.");
    }

    try {
      rentalRepository.deleteById(id);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  //Validation examples below (likely too complicated)
//  private void validateProducts(Purchase purchase) {
//    // Get products from each line item
//    Set<LineItem> lineItemSet = purchase.getProducts();
//
//    // If no products throw bad request
//    if (lineItemSet == null || lineItemSet.size() == 0) {
//      throw new BadRequest(StringConstants.PURCHASE_HAS_NO_PRODUCTS);
//    }
//
//    // Set list of products that are not able to be processed
//    List<Product> inactiveProducts = new ArrayList<>();
//    List<Product> insufficientStock = new ArrayList<>();
//
//    //get products with insufficient inventory
//    Map<Long, Product> lockedProducts = getProductMap(lineItemSet);
//
//    // Loop through each lineItem for purchase to get product info
//    lineItemSet.forEach(lineItem -> {
//
//      // retrieve full product information from the database
//      Product product = lockedProducts.get(lineItem.getProduct().getId());
//
//      // if product status is not active add the product to list of items unable to be processed
//      if (product.getActive() == null || !product.getActive() || product == null) {
//        inactiveProducts.add(product);
//      }
//      if (product != null
//          && lockedProducts.get(product.getId()).getQuantity() < lineItem.getQuantity()) {
//        insufficientStock.add(product);
//      }
//
//      //set lineItem product to product instance from database
//      lineItem.setProduct(product);
//
//    });
//    if (inactiveProducts.size() > 0 || insufficientStock.size() > 0) {
//      logger.error(StringConstants.UNPROCESSABLE_ITEMS);
//    }
//    if (inactiveProducts.size() > 0 && insufficientStock.size() > 0) {
//      Map<String, List<Product>> unprocessableMap = new HashMap();
//      unprocessableMap.put(StringConstants.PRODUCT_INACTIVE, inactiveProducts);
//      unprocessableMap.put(StringConstants.INSUFFICIENT_INVENTORY, insufficientStock);
//
//      throw new MultipleUnprocessableContent(StringConstants.UNPROCESSABLE_ITEMS, unprocessableMap);
//    }
//
//    if (inactiveProducts.size() > 0) {
//      throw new UnprocessableContent(StringConstants.PRODUCT_INACTIVE, inactiveProducts);
//    }
//
//    // If unprocessable list has items throw Unprocessable Content error with list of products
//    if (insufficientStock.size() > 0) {
//      throw new UnprocessableContent(StringConstants.INSUFFICIENT_INVENTORY, insufficientStock);
//    }
//  }

//
//  public Map<Long, Product> getProductMap(Set<LineItem> lineItems) {
//    List<Long> ids = lineItems.stream()
//        .map(p -> p.getProduct().getId())
//        .collect(Collectors.toList());
//    return movieService.getProductsByIds(ids).stream().collect(Collectors
//        .toMap(Product::getId, Function.identity()));
//  }


}

