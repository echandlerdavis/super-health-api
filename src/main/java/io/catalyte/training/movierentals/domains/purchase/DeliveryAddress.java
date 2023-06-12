package io.catalyte.training.movierentals.domains.purchase;

import java.util.Objects;
import javax.persistence.Embeddable;

/**
 * Describes the object for the delivery address of the purchase
 */
@Embeddable
public class DeliveryAddress {

  private String firstName;
  private String lastName;
  private String deliveryStreet;
  private String deliveryStreet2;
  private String deliveryCity;
  private String deliveryState;
  private int deliveryZip;

  public DeliveryAddress() {
  }

  public DeliveryAddress(String firstName, String lastName, String deliveryStreet,
      String deliveryStreet2, String deliveryCity, String deliveryState, int deliveryZip) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.deliveryStreet = deliveryStreet;
    this.deliveryStreet2 = deliveryStreet2;
    this.deliveryCity = deliveryCity;
    this.deliveryState = deliveryState;
    this.deliveryZip = deliveryZip;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getDeliveryStreet() {
    return deliveryStreet;
  }

  public void setDeliveryStreet(String deliveryStreet) {
    this.deliveryStreet = deliveryStreet;
  }

  public String getDeliveryStreet2() {
    return deliveryStreet2;
  }

  public void setDeliveryStreet2(String deliveryStreet2) {
    this.deliveryStreet2 = deliveryStreet2;
  }

  public String getDeliveryCity() {
    return deliveryCity;
  }

  public void setDeliveryCity(String deliveryCity) {
    this.deliveryCity = deliveryCity;
  }

  public String getDeliveryState() {
    return deliveryState;
  }

  public void setDeliveryState(String deliveryState) {
    this.deliveryState = deliveryState;
  }

  public int getDeliveryZip() {
    return deliveryZip;
  }

  public void setDeliveryZip(int deliveryZip) {
    this.deliveryZip = deliveryZip;
  }

  @Override
  public String toString() {
    return "DeliveryAddress{" +
        "firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", deliveryStreet='" + deliveryStreet + '\'' +
        ", deliveryStreet2='" + deliveryStreet2 + '\'' +
        ", deliveryCity='" + deliveryCity + '\'' +
        ", deliveryState='" + deliveryState + '\'' +
        ", deliveryZip=" + deliveryZip +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeliveryAddress)) {
      return false;
    }
    DeliveryAddress that = (DeliveryAddress) o;
    return deliveryZip == that.deliveryZip && Objects.equals(firstName, that.firstName)
        && Objects.equals(lastName, that.lastName) && Objects.equals(
        deliveryStreet, that.deliveryStreet) && Objects.equals(deliveryStreet2,
        that.deliveryStreet2) && Objects.equals(deliveryCity, that.deliveryCity)
        && Objects.equals(deliveryState, that.deliveryState);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, deliveryStreet, deliveryStreet2, deliveryCity,
        deliveryState, deliveryZip);
  }
}
