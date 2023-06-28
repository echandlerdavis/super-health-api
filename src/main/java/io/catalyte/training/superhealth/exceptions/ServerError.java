package io.catalyte.training.superhealth.exceptions;

/**
 * A custom exception for internal service errors.
 */
public class ServerError extends RuntimeException {

  public ServerError() {
  }

  public ServerError(String message) {
    super(message);
  }
}
