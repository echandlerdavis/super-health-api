package io.catalyte.training.movierentals.exceptions;

public class BadRequest extends RuntimeException {

  public BadRequest() {
  }

  public BadRequest(String message) {
    super(message);
  }
}
