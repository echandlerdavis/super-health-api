package io.catalyte.training.sportsproducts.exceptions;

import java.util.Map;

public class MultipleUnprocessableContent extends RuntimeException{

  private Map unprocessed;

  public MultipleUnprocessableContent(String message, Map unprocessed){
    super(message);
    this.unprocessed = unprocessed;
  }

  public Map getUnprocessed() {
    return unprocessed;
  }

  public void setUnprocessed(Map unprocessed) {
    this.unprocessed = unprocessed;
  }
}
