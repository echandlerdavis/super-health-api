package io.catalyte.training.superhealth.exceptions;

public class ServiceUnavailable extends RuntimeException{

  public ServiceUnavailable() {
  }

  public ServiceUnavailable(String message) {
    super(message);
  }
}
