package com.finance.app.domain.exception;

public class InvalidFirebaseTokenException extends RuntimeException {

    public InvalidFirebaseTokenException(String message) {
        super(message);
    }

    public static InvalidFirebaseTokenException invalid() {
        return new InvalidFirebaseTokenException("Invalid Firebase token. Please authenticate again with Google");
    }

    public static InvalidFirebaseTokenException expired() {
        return new InvalidFirebaseTokenException("Firebase token has expired. Please authenticate again with Google");
    }

}
