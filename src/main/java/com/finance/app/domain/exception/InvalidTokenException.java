package com.finance.app.domain.exception;

public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }

    public static InvalidTokenException expired() {
        return new InvalidTokenException("Token has expired. Please login again to obtain a new token");
    }

    public static InvalidTokenException invalid() {
        return new InvalidTokenException("Invalid token. Please check the token provided in the Authorization header");
    }

    public static InvalidTokenException absent() {
        return new InvalidTokenException(
                "Token is missing. Please provide the JWT token in the Authorization header: Bearer <token>");
    }

}
