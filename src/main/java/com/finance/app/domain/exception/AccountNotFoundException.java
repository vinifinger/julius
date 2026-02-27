package com.finance.app.domain.exception;

import java.util.UUID;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(UUID id) {
        super("Account not found with id: " + id);
    }

}
