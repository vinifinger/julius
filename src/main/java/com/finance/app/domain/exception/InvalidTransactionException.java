package com.finance.app.domain.exception;

public class InvalidTransactionException extends RuntimeException {

    public InvalidTransactionException(String message) {
        super(message);
    }

}
