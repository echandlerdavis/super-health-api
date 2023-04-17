package io.catalyte.training.sportsproducts.domains.promotions;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

/**
 * A DTO (Data Transfer Object) representing the input data for creating a promotional code.
 */
public class PromotionalCodeDTO {

    @NotEmpty
    private String title;

    @NotEmpty
    private String description;

    @NotNull
    private PromotionalCodeType type;

    @NotNull
    @Positive
    private BigDecimal rate;

    /**
     * Constructs a new PromotionalCodeDTO instance.
     *
     * @param title The title of the promotional code.
     * @param description The description of the promotional code.
     * @param type The type of the promotional code.
     * @param rate The rate of the promotional code.
     */
    public PromotionalCodeDTO(String title, String description, PromotionalCodeType type, BigDecimal rate) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.rate = rate;
    }

    public PromotionalCodeDTO() {

    }

    /**
     * Returns the title of the promotional code.
     *
     * @return The title of the promotional code.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the promotional code.
     *
     * @param title The title of the promotional code.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the description of the promotional code.
     *
     * @return The description of the promotional code.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the promotional code.
     *
     * @param description The description of the promotional code.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the type of the promotional code.
     *
     * @return The type of the promotional code.
     */
    public PromotionalCodeType getType() {
        return type;
    }

    /**
     * Sets the type of the promotional code.
     *
     * @param type The type of the promotional code.
     */
    public void setType(PromotionalCodeType type) {
        this.type = type;
    }

    /**
     * Returns the rate of the promotional code.
     *
     * @return The rate of the promotional code.
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * Sets the rate of the promotional code.
     *
     * @param rate The rate of the promotional code.
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }
}