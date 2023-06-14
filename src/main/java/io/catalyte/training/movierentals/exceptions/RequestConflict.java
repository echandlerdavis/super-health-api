package io.catalyte.training.movierentals.exceptions;

public class RequestConflict extends RuntimeException{

  public RequestConflict() {
  }

  public RequestConflict(String message) {
    super(message);
  }
}
