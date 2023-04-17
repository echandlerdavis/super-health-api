package io.catalyte.training.sportsproducts.domains.promotions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * This class represents a promotional code, which is used to offer discounts
 * to customers when purchasing products.
 */
@Entity
public class PromotionalCode {

    private String code;
    private String title;
    private String description;
    private PromotionalCodeType type;
    private BigDecimal rate;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Constructs a new PromotionalCode object with the specified title, description,
     * type, and rate.
     *
     * @param title the title of the promotional code
     * @param description the description of the promotional code
     * @param type the type of the promotional code (either percent or flat)
     * @param rate the rate of the promotional code (a percentage or flat dollar amount)
     */
    public PromotionalCode(String title, String description, PromotionalCodeType type, BigDecimal rate) {
        this.title = title;
        this.description = description;
        this.type = type;
        this.rate = rate;
    }

    public PromotionalCode() {


    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PromotionalCodeType getType() {
        return type;
    }

    public void setType(PromotionalCodeType type) {
        this.type = type;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}