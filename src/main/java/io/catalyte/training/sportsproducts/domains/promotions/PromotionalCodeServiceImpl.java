package io.catalyte.training.sportsproducts.domains.promotions;

import io.catalyte.training.sportsproducts.exceptions.BadRequest;
import io.catalyte.training.sportsproducts.exceptions.ServerError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * This service class provides functionality related to promotional codes.
 */
@Service
public class PromotionalCodeServiceImpl implements PromotionalCodeService {

    private final Logger logger = LogManager.getLogger(PromotionalCodeServiceImpl.class);

    private PromotionalCodeRepository promotionalCodeRepository;

    /**
     * Constructs a PromotionalCodeServiceImpl object with the given PromotionalCodeRepository.
     */
    @Autowired
    public PromotionalCodeServiceImpl(PromotionalCodeRepository promotionalCodeRepository) {
        this.promotionalCodeRepository = promotionalCodeRepository;
    }

    /**
     * Creates a new promotional code using the information provided in the given DTO.
     *
     * @param promotionalCodeDTO The PromotionalCodeDTO containing the information for the new promotional code.
     * @return The created promotional code.
     * @throws IllegalArgumentException if the given promotional code is invalid.
     */
    @Override
    public PromotionalCode createPromotionalCode(PromotionalCodeDTO promotionalCodeDTO) throws IllegalArgumentException {
        PromotionalCode promotionalCode = new PromotionalCode();
        promotionalCode.setTitle(promotionalCodeDTO.getTitle());
        promotionalCode.setDescription(promotionalCodeDTO.getDescription());
        promotionalCode.setType(promotionalCodeDTO.getType());
        promotionalCode.setRate(promotionalCodeDTO.getRate());

        try {
            // validate the promotional code before saving
            if (promotionalCode.getType() == null || promotionalCode.getRate() == null) {
                throw new IllegalArgumentException("Promotional code type and rate cannot be null");
            }

            if (promotionalCode.getType() == PromotionalCodeType.FLAT && promotionalCode.getRate().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Flat promotional code rate must be greater than zero");
            }

            if (promotionalCode.getType() == PromotionalCodeType.PERCENT && (promotionalCode.getRate().compareTo(BigDecimal.ZERO) <= 0 || promotionalCode.getRate().compareTo(BigDecimal.valueOf(100)) > 0)) {
                throw new IllegalArgumentException("Percentage promotional code rate must be greater than zero and less than or equal to 100");
            }
        } catch (IllegalArgumentException ex) {
            throw new BadRequest(ex.getMessage());
        }

        try {
            return promotionalCodeRepository.save(promotionalCode);
        } catch (DataAccessException e) {
            logger.error(e.getMessage());
            throw new BadRequest(e.getMessage());
        }
    }

    /**
     * Validates that the given promotional code is valid.
     *
     * @param promotionalCode The promotional code entity to validate.
     * @throws ServerError if a data acess exception occurs.
     * @throws IllegalArgumentException if the given promotional code is invalid.
     */
    private void validatePromotionalCode(PromotionalCode promotionalCode) throws IllegalArgumentException {
        try {
            if (promotionalCode.getType() == null || promotionalCode.getRate() == null) {
                throw new IllegalArgumentException("Invalid promotional code: missing type or rate");
            }

            if (promotionalCode.getType() == PromotionalCodeType.FLAT && promotionalCode.getRate().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Invalid promotional code: flat rate must be greater than zero");
            }

            if (promotionalCode.getType() == PromotionalCodeType.PERCENT && (promotionalCode.getRate().compareTo(BigDecimal.ZERO) <= 0 || promotionalCode.getRate().compareTo(BigDecimal.valueOf(100)) > 0)) {
                throw new IllegalArgumentException("Invalid promotional code: percent rate must be between 0 and 100");
            }
        } catch (DataAccessException ex) {
            String errorMessage = "Error validating promotional code: " + ex.getMessage();
            logger.error(errorMessage);
            throw new ServerError(errorMessage);
        }
    }

    /**
     *  Returns all the promotional codes in the system
     *
     * @return The list of all promotional codes.
     */
    @Override
    public List<PromotionalCode> getAllPromotionalCodes() {
        try {
            return promotionalCodeRepository.findAll();
        } catch (DataAccessException e) {
            // Log the error message using Log4j
            logger.error("Error getting all promotional codes: " + e.getMessage());
            // Throw a ServerError exception with the caught exception's message
            throw new ServerError("Error getting all promotional codes: " + e.getMessage());
        }
    }

    /**
     *  Returns the promotional code with the given title
     *
     * @param title The title of the promotional code to retrieve.
     * @return The promotional code with the given Title, or null if no such promotional code exist.
     */
    @Override
    public PromotionalCode getPromotionalCodeByTitle(String title) {
       try {
           List<PromotionalCode> codes = (List<PromotionalCode>) promotionalCodeRepository.findByTitle(title);
           if (codes != null && !codes.isEmpty()) {
               return codes.get(0);
           }
           return null;
       } catch (DataAccessException e) {
           // Log the error message
           logger.error("Error getting promotional code by title: " + e.getMessage());
           // Throw a ServerError exception with the caught exception's message
           throw new ServerError(e.getMessage());
       }
    }

    /**
     * Applies the given promotional code to the given price and returns the discounted price.
     *
     * @param title The title of the promotional code to apply.
     * @param price The original price before the discount.
     * @return The discounted price.
     */
    public BigDecimal applyPromotionalCode(String title, BigDecimal price) {
        try {
            PromotionalCode promotionalCode = (PromotionalCode) promotionalCodeRepository.findByTitle(title);
            if (promotionalCode == null || promotionalCode.getType() == null || promotionalCode.getRate() == null) {
                return price.setScale(1, RoundingMode.HALF_UP);
            }

            BigDecimal discount;
            if (promotionalCode.getType() == PromotionalCodeType.FLAT) {
                if (promotionalCode.getRate().compareTo(BigDecimal.ZERO) <= 0) {
                    return price.setScale(1, RoundingMode.HALF_UP);
                }
                discount = promotionalCode.getRate();
            } else if (promotionalCode.getType() == PromotionalCodeType.PERCENT) {
                if (promotionalCode.getRate().compareTo(BigDecimal.ZERO) <= 0 || promotionalCode.getRate().compareTo(BigDecimal.valueOf(100)) > 0) {
                    return price.setScale(1, RoundingMode.HALF_UP);
                }
                discount = price.multiply(promotionalCode.getRate().setScale(1, RoundingMode.DOWN));
            } else {
                return price.setScale(1, RoundingMode.HALF_UP);
            }
            return price.subtract(discount).setScale(1, RoundingMode.HALF_UP);
        } catch (DataAccessException e) {
            logger.error("An error occurred while applying promotional code: " + e.getMessage());
            throw new ServerError(e.getMessage());
        }
    }

}

