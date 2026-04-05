package com.finance.app.domain.exception;

import java.util.UUID;

public class AccountHasPendingInstallmentsException extends RuntimeException {

    public AccountHasPendingInstallmentsException(UUID accountId) {
        super("Cannot delete account " + accountId + ": there are pending installments linked to this account");
    }

}
