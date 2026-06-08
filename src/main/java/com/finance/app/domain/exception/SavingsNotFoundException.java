package com.finance.app.domain.exception;

import java.util.UUID;

public class SavingsNotFoundException extends RuntimeException {
    public SavingsNotFoundException(UUID id) {
        super("Savings vault with ID " + id + " not found");
    }
}
