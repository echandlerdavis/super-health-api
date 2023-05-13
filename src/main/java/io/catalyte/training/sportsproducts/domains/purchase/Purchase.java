package io.catalyte.training.sportsproducts.domains.purchase;

import io.catalyte.training.sportsproducts.domains.promotions.PromotionalCode;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Describes a purchase object that holds the information for a transaction
 */
@Entity
public class Purchase {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToMany(mappedBy = "purchase")
  @OnDelete(action = OnDeleteAction.CASCADE)
  private Set<LineItem> products;

  private DeliveryAddress deliveryAddress;

  private BillingAddress billingAddress;

  private CreditCard creditCard;

  @ManyToOne
  private PromotionalCode promoCode;

  public Purchase() {
    billingAddress = new BillingAddress();
    deliveryAddress = new DeliveryAddress();
    creditCard = new CreditCard();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Set<LineItem> getProducts() {
    return products;
  }

  public void setProducts(Set<LineItem> products) {
    this.products = products;
  }

  public DeliveryAddress getDeliveryAddress() {
    return deliveryAddress;
  }

  public void setDeliveryAddress(DeliveryAddress deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }

  public BillingAddress getBillingAddress() {
    return billingAddress;
  }

  public void setBillingAddress(BillingAddress billingAddress) {
    this.billingAddress = billingAddress;
  }

  public CreditCard getCreditCard() {
    return creditCard;
  }

  public void setCreditCard(CreditCard creditCard) {
    this.creditCard = creditCard;
  }

  public PromotionalCode getPromoCode() {
    return promoCode;
  }

  public void setPromoCode(
      PromotionalCode promoCode) {
    this.promoCode = promoCode;
  }

  @Override
  public String toString() {
    return "Purchase{" +
        "id=" + id +
        ", deliveryAddress=" + deliveryAddress +
        ", billingAddress=" + billingAddress +
        ", creditCard=" + creditCard +
        ", promoCode=" + promoCode +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Purchase)) {
      return false;
    }
    Purchase purchase = (Purchase) o;
    return id.equals(purchase.id) && products.equals(purchase.products) && deliveryAddress.equals(
        purchase.deliveryAddress) && billingAddress.equals(purchase.billingAddress)
        && creditCard.equals(purchase.creditCard) && Objects.equals(promoCode,
        purchase.promoCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, products, deliveryAddress, billingAddress, creditCard, promoCode);
  }
}