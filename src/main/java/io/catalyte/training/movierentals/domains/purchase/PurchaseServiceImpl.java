package io.catalyte.training.movierentals.domains.purchase;

import io.catalyte.training.movierentals.constants.StringConstants;
import io.catalyte.training.movierentals.domains.movie.Product;
import io.catalyte.training.movierentals.domains.movie.MovieService;
import io.catalyte.training.movierentals.domains.promotions.PromotionalCode;
import io.catalyte.training.movierentals.domains.promotions.PromotionalCodeService;
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
public class PurchaseServiceImpl implements PurchaseService {

  private final Logger logger = LogManager.getLogger(PurchaseServiceImpl.class);

  PurchaseRepository purchaseRepository;
  MovieService movieService;
  LineItemRepository lineItemRepository;
  PromotionalCodeService promoCodeService;

  @Autowired
  public PurchaseServiceImpl(PurchaseRepository purchaseRepository, MovieService movieService,
      LineItemRepository lineItemRepository, PromotionalCodeService promoCodeService) {
    this.purchaseRepository = purchaseRepository;
    this.movieService = movieService;
    this.lineItemRepository = lineItemRepository;
    this.promoCodeService = promoCodeService;
  }

  /**
   * Retrieves all purchases from the database
   *
   * @return
   */
  public List<Purchase> findAllPurchases() {
    try {
      return purchaseRepository.findAll();
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  /**
   * Search for all purchases made with the given email attached.
   *
   * @param email String
   * @return List of Purchase objects
   */

  @Override
  public List<Purchase> findByBillingAddressEmail(String email) {
    try {
      return purchaseRepository.findByBillingAddressEmail(email);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
  }

  @Override
  public List<StateEnumDTO> getStateOptions() {
    return StateEnum.getStatesJsonList();
  }

  /**
   * Persists a purchase to the database
   *
   * @param newPurchase - the purchase to persist
   * @return the persisted purchase with ids
   */
  public Purchase savePurchase(Purchase newPurchase) {
    //credit card validation
    CreditCard creditCard = newPurchase.getCreditCard();
    validateCreditCard(creditCard);
    //product validation
    validateProducts(newPurchase);
    //products in newPurchase should now be products pulled from database
    //calculate the shipping charge and apply if appropriate
    if (newPurchase.applyShippingCharge()) {
      newPurchase.setShippingCharge(
          StateEnum.getShippingByName(newPurchase.getDeliveryAddress().getDeliveryState())
      );
    } else {
      //else set shipping charge to 0
      newPurchase.setShippingCharge(0.00);
    }
    //promocode validation
    PromotionalCode appliedCode = newPurchase.getPromoCode();
    if (appliedCode != null) {
      try {
        PromotionalCode actualCode = promoCodeService.getPromotionalCodeByTitle(
            appliedCode.getTitle());
        //ensure that the promoCode is valid by replacing the client supplied one with the
        //one from the database
        newPurchase.setPromoCode(actualCode);
      } catch (BadRequest | ResourceNotFound e) {
        //this means the promocode is either not active or not found
        newPurchase.setPromoCode(null);
      }
    }
    //add date of today
    newPurchase.setDate(new Date());

    //Handle ID received from UI and create savedPurchase
    newPurchase.setId(null);
    Purchase savedPurchase;
    //get the saved purchase
    try {
      savedPurchase = purchaseRepository.save(newPurchase);
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServerError(e.getMessage());
    }
    //update the id on newPurchase
    newPurchase.setId(savedPurchase.getId());

    // after the purchase is persisted and has an id, we need to handle its lineitems and persist them as well
    handleLineItems(newPurchase);
    //update inventory quantities
    updateInventory(newPurchase);
    savedPurchase.setProducts(lineItemRepository.findByPurchase(newPurchase));
    //don't need purchase attached to line items
    for (LineItem line : savedPurchase.getProducts()) {
      line.setPurchase(null);
    }

    return savedPurchase;
  }

  /**
   * This helper method retrieves product information for each line item and persists it
   *
   * @param purchase - the purchase obj  ect to handle lineitems for
   */
  private void handleLineItems(Purchase purchase) {
    //all LineItems should have already had their product set to
    //the product retrieved from the database
    Set<LineItem> itemsList = purchase.getProducts();

    if (itemsList != null) {
      itemsList.forEach(lineItem -> {

        // retrieve full product information from the database
        Product product = movieService.getProductById(lineItem.getProduct().getId());

        // set the product info into the lineitem
        if (product != null) {
          lineItem.setProduct(product);
        }
        //get rid of the id
        lineItem.setId(null);

        // set the purchase on the line item
        lineItem.setPurchase(purchase);

        // persist the populated lineitem
        try {
          lineItemRepository.save(lineItem);
        } catch (DataAccessException e) {
          logger.error(e.getMessage());
          throw new ServerError(e.getMessage());
        }
      });
    }
  }

  private void validateProducts(Purchase purchase) {
    // Get products from each line item
    Set<LineItem> lineItemSet = purchase.getProducts();

    // If no products throw bad request
    if (lineItemSet == null || lineItemSet.size() == 0) {
      throw new BadRequest(StringConstants.PURCHASE_HAS_NO_PRODUCTS);
    }

    // Set list of products that are not able to be processed
    List<Product> inactiveProducts = new ArrayList<>();
    List<Product> insufficientStock = new ArrayList<>();

    //get products with insufficient inventory
    Map<Long, Product> lockedProducts = getProductMap(lineItemSet);

    // Loop through each lineItem for purchase to get product info
    lineItemSet.forEach(lineItem -> {

      // retrieve full product information from the database
      Product product = lockedProducts.get(lineItem.getProduct().getId());

      // if product status is not active add the product to list of items unable to be processed
      if (product.getActive() == null || !product.getActive() || product == null) {
        inactiveProducts.add(product);
      }
      if (product != null
          && lockedProducts.get(product.getId()).getQuantity() < lineItem.getQuantity()) {
        insufficientStock.add(product);
      }

      //set lineItem product to product instance from database
      lineItem.setProduct(product);

    });
    if (inactiveProducts.size() > 0 || insufficientStock.size() > 0) {
      logger.error(StringConstants.UNPROCESSABLE_ITEMS);
    }
    if (inactiveProducts.size() > 0 && insufficientStock.size() > 0) {
      Map<String, List<Product>> unprocessableMap = new HashMap();
      unprocessableMap.put(StringConstants.PRODUCT_INACTIVE, inactiveProducts);
      unprocessableMap.put(StringConstants.INSUFFICIENT_INVENTORY, insufficientStock);

      throw new MultipleUnprocessableContent(StringConstants.UNPROCESSABLE_ITEMS, unprocessableMap);
    }

    if (inactiveProducts.size() > 0) {
      throw new UnprocessableContent(StringConstants.PRODUCT_INACTIVE, inactiveProducts);
    }

    // If unprocessable list has items throw Unprocessable Content error with list of products
    if (insufficientStock.size() > 0) {
      throw new UnprocessableContent(StringConstants.INSUFFICIENT_INVENTORY, insufficientStock);
    }
  }

  /**
   * Helper method that checks the credit card within a purchase
   * <p>
   * Makes sure no fields are null and all credit card fields are valid
   * <p>
   * Calls all other helper methods that validate each card field separately
   *
   * @param creditCard credit card to be validated
   */
  private void validateCreditCard(CreditCard creditCard) {
    validateCreditCardProvided(creditCard);
    validateCreditCardNumber(creditCard);
    validateCreditCardCVV(creditCard);
    validateCreditCardHolder(creditCard);
    validateCreditCardExpiration(creditCard);
  }

  /**
   * Helper method that checks credit card was given (not null)
   *
   * @param creditCard credit card to be validated
   */
  private void validateCreditCardProvided(CreditCard creditCard) {
    if (creditCard == null) {
      throw new BadRequest(StringConstants.CARD_NOT_PROVIDED);
    }
  }

  /**
   * Helper method that checks credit card number is not null and is 16 digits
   *
   * @param creditCard credit card to be validated
   */
  private void validateCreditCardNumber(CreditCard creditCard) {
    String creditCardNumber = creditCard.getCardNumber();

    if (creditCardNumber == null || !creditCardNumber.matches("[0-9]{16}")) {
      throw new BadRequest(StringConstants.CARD_NUMBER_INVALID);
    }
  }

  /**
   * Helper method that checks credit card CVV is not null and is 3 digits
   *
   * @param creditCard credit card to be validated
   */
  private void validateCreditCardCVV(CreditCard creditCard) {
    String cvv = creditCard.getCvv();
    if (cvv == null || cvv.length() != 3 || !cvv.matches("[0-9]{3}")) {
      throw new BadRequest(StringConstants.CARD_CVV_INVALID);
    }
  }

  /**
   * Helper method that checks credit card holder not null
   *
   * @param creditCard credit card to be validated
   */
  private void validateCreditCardHolder(CreditCard creditCard) {
    String cardHolder = creditCard.getCardholder();
    if (cardHolder == null || !cardHolder.matches("\\D+")) {
      throw new BadRequest(StringConstants.CARD_HOLDER_INVALID);
    }
  }

  /**
   * Helper method that checks that a credit card's is not expired and is entered in pattern of
   * MM/YY
   *
   * @param creditCard credit card to be validated
   */
  private void validateCreditCardExpiration(CreditCard creditCard) {
    String expiration = creditCard.getExpiration();

    // Check expiration data is not null
    if (expiration == null) {
      throw new BadRequest("Expiration date can not be null");
    }

    // Check expiration date is entered as MM/YY
    String expirationRegEx = "^(0[1-9]||1[0-2])/[0-9]{2}$";

    if (!expiration.matches(expirationRegEx)) {
      throw new BadRequest(StringConstants.CARD_EXPIRATION_INVALID_FORMAT);
    }

    // Check Expiration date is not past
    Date expiry;

    // Attempt to parse the expiration string in MM/YY format
    try {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
      simpleDateFormat.setLenient(false);
      expiry = simpleDateFormat.parse(expiration);
      int expMonth = expiry.getMonth();
      int expYear = expiry.getYear();
      expMonth += 1;
      if (expMonth == 13) {
        expMonth = 1;
        expYear += 1;
      }
      expiry.setMonth(expMonth);
      expiry.setYear(expYear);
    } catch (ParseException e) {
      throw new BadRequest(StringConstants.CARD_EXPIRATION_INVALID_FORMAT);
    }

    // Set expired to be true or false if cards expiration date is before the current date
    boolean expired = expiry.before(new Date());

    if (expired) {
      throw new BadRequest(StringConstants.CARD_EXPIRED);
    }
  }

  public Map<Long, Product> getProductMap(Set<LineItem> lineItems) {
    List<Long> ids = lineItems.stream()
        .map(p -> p.getProduct().getId())
        .collect(Collectors.toList());
    return movieService.getProductsByIds(ids).stream().collect(Collectors
        .toMap(Product::getId, Function.identity()));
  }

  /**
   * Updates product inventory in the database based on a purchase
   *
   * @param purchase Purchase whose LineItem list will be used to reduce inventory quantities in the
   *                 database
   */
  private void updateInventory(Purchase purchase) {
    //purchase products at this point should be products from database, not ones passed
    //from client
    purchase.getProducts().forEach(lineItem -> {
      Product product = lineItem.getProduct();
      product.setQuantity(product.getQuantity() - lineItem.getQuantity());
      try {
        movieService.saveProduct(product);
      } catch (DataAccessException dae) {
        logger.error("Failed to update inventory for product " + product.getId());
        throw new ServerError(dae.getMessage());
      }
    });

  }

}

