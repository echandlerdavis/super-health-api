package io.catalyte.training.sportsproducts.domains.purchase;

public class StateEnumDTO {

  private Double shippingCost;
  private String fullName;

  public StateEnumDTO() {
  }

  public StateEnumDTO(StateEnum state) {
    this.shippingCost = state.shippingCost;
    this.fullName = state.fullName;
  }

  public Double getShippingCost() {
    return shippingCost;
  }

  public void setShippingCost(Double shippingCost) {
    this.shippingCost = shippingCost;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

}
