package io.catalyte.training.sportsproducts.domains.promotions;

import java.util.List;

/**
 * This interface defines the functionality related to promotional codes.
 */
public interface PromotionalCodeService {

    /**
     * Creates a new promotional code using the information in the given PromotionalCodeDTO.
     *
     * @param promotionalCodeDTO The PromotionalCodeDTO containing the information for the new promotional code.
     * @return The created PromotionalCode.
     * @throws IllegalArgumentException
     */
    PromotionalCode createPromotionalCode(PromotionalCodeDTO promotionalCodeDTO) throws IllegalArgumentException;

    /**
     * Returns a list of all promotional codes
     *
     * @return A list of all promotional codes
     */

    List<PromotionalCode> getAllPromotionalCodes();

    PromotionalCode getPromotionalCodeByTitle(String title);
}