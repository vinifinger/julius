package com.finance.app.domain.exception;

public class DuplicateTransactionException extends RuntimeException {
    public DuplicateTransactionException(String externalId) {
        super("Transaction with external ID " + externalId + " already exists");
    }
}
