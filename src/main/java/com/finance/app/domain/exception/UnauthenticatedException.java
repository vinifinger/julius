package com.finance.app.domain.exception;

public class UnauthenticatedException extends RuntimeException {
    public UnauthenticatedException() {
        super("User is not authenticated or session is invalid");
    }

    public UnauthenticatedException(String message) {
        super(message);
    }
}
