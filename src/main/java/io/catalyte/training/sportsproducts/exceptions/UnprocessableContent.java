package io.catalyte.training.sportsproducts.exceptions;

import java.util.List;

public class UnprocessableContent extends RuntimeException {

  private List unprocessed;

  public UnprocessableContent(String message, List unprocessed) {
    super(message);
    this.unprocessed = unprocessed;
  }

  public List getUnprocessed() {
    return unprocessed;
  }

  public void setUnprocessed(List unprocessed) {
    this.unprocessed = unprocessed;
  }

}
