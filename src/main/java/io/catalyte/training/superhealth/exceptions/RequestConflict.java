package io.catalyte.training.superhealth.exceptions;

public class RequestConflict extends RuntimeException{

  public RequestConflict() {
  }

  public RequestConflict(String message) {
    super(message);
  }
}
