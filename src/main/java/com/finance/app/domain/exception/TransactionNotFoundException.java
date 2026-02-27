package com.finance.app.domain.exception;

import java.util.UUID;

public class TransactionNotFoundException extends RuntimeException {

    public TransactionNotFoundException(UUID id) {
        super("Transaction not found with id: " + id);
    }

}
