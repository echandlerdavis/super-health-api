package io.catalyte.training.sportsproducts.domains.purchase;

import io.catalyte.training.sportsproducts.constants.StringConstants;
import io.catalyte.training.sportsproducts.domains.product.Product;
import io.catalyte.training.sportsproducts.domains.product.ProductService;
import io.catalyte.training.sportsproducts.exceptions.BadRequest;
import io.catalyte.training.sportsproducts.exceptions.ServerError;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PurchaseServiceImpl implements PurchaseService {

    private final Logger logger = LogManager.getLogger(PurchaseServiceImpl.class);

    PurchaseRepository purchaseRepository;
    ProductService productService;
    LineItemRepository lineItemRepository;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, ProductService productService,
                               LineItemRepository lineItemRepository) {
        this.purchaseRepository = purchaseRepository;
        this.productService = productService;
        this.lineItemRepository = lineItemRepository;
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
     * Persists a purchase to the database
     *
     * @param newPurchase - the purchase to persist
     * @return the persisted purchase with ids
     */
    public Purchase savePurchase(Purchase newPurchase) {
        CreditCard creditCard = newPurchase.getCreditCard();
        validateCreditCard(creditCard);
        try {
            purchaseRepository.save(newPurchase);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new ServerError(e.getMessage());
        }

        // after the purchase is persisted and has an id, we need to handle its lineitems and persist them as well
        handleLineItems(newPurchase);

        return newPurchase;
    }

    /**
     * This helper method retrieves product information for each line item and persists it
     *
     * @param purchase - the purchase object to handle lineitems for
     */
    private void handleLineItems(Purchase purchase) {
        Set<LineItem> itemsList = purchase.getProducts();

        if (itemsList != null) {
            itemsList.forEach(lineItem -> {

                // retrieve full product information from the database
                Product product = productService.getProductById(lineItem.getProduct().getId());

                // set the product info into the lineitem
                if (product != null) {
                    lineItem.setProduct(product);
                }

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
    public void validateCreditCardHolder(CreditCard creditCard) {
        String cardHolder = creditCard.getCardholder();
        if (cardHolder == null || !cardHolder.matches("\\D+")) {
            throw new BadRequest(StringConstants.CARD_HOLDER_NULL);
        }
    }

    /**
     * Helper method that checks that a credit card's is not expired and is entered in pattern of MM/YY
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
        String expirationRegEx = "^(0[0-9]||1[0-2])/[0-9]{2}$";
        if (!expiration.matches(expirationRegEx)) {
            throw new BadRequest(StringConstants.CARD_EXPIRATION_INVALID_FORMAT);
        }

        // Parse expiration string to get values of the month and the year
        Integer expMonth = Integer.parseInt(expiration.substring(0, 2));
        Integer expYear = Integer.parseInt("20" + expiration.substring(3));

        // Get the values of the current month and the current year
        Integer currMonth = LocalDate.now().getMonthValue();
        Integer currYear = LocalDate.now().getYear();

        // Check that the expiration is not before the current year
        // Or if it is the current year, the month is a future month
        if ((expYear < currYear) || (expYear.equals(currYear) && expMonth <= currMonth))
            throw new BadRequest(StringConstants.CARD_EXPIRED);
    }

}

