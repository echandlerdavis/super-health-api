package io.catalyte.training.sportsproducts.domains.promotions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * This class represents a promotional code, which is used to offer discounts to customers when
 * purchasing products.
 */
@Entity
public class PromotionalCode {

  private String title;
  private String description;
  private PromotionalCodeType type;
  private BigDecimal rate;

  private Date startDate;
  private Date endDate;
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonIgnore
  private Long id;

  /**
   * Constructs a new PromotionalCode object with the specified title, description, type, and rate.
   *
   * @param title       the title of the promotional code
   * @param description the description of the promotional code
   * @param type        the type of the promotional code (either percent or flat)
   * @param rate        the rate of the promotional code (a percentage or flat dollar amount)
   */
  public PromotionalCode(String title, String description, PromotionalCodeType type,
      BigDecimal rate) {
    this.title = title;
    this.description = description;
    this.type = type;
    this.rate = rate;
  }

  public PromotionalCode(String title, String description, PromotionalCodeType type,
      BigDecimal rate, Date startDate, Date endDate) {
    this(title, description, type, rate);
    this.startDate = startDate;
    this.endDate = endDate;
  }

  public PromotionalCode() {

  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  @Column(unique = true)
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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof PromotionalCode)) {
      return false;
    }
    PromotionalCode that = (PromotionalCode) o;
    return title.equals(that.title) && Objects.equals(description, that.description)
        && type == that.type && rate.equals(that.rate) && id.equals(that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(title, description, type, rate, id);
  }

  @Override
  public String toString() {
    return "PromotionalCode{" +
        "title='" + title + '\'' +
        ", description='" + description + '\'' +
        ", type=" + type +
        ", rate=" + rate +
        ", id=" + id +
        '}';
  }
}