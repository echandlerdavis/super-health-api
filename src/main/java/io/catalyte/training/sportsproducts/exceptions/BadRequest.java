package io.catalyte.training.sportsproducts.exceptions;

public class BadRequest extends RuntimeException {
    public BadRequest() {
    }

    public BadRequest(String message) {
        super(message);
    }
}
