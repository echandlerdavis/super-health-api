package io.catalyte.training.movierentals.exceptions;

public class ServiceUnavailable extends RuntimeException{

  public ServiceUnavailable() {
  }

  public ServiceUnavailable(String message) {
    super(message);
  }
}
