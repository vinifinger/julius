package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record SavingsTransactionRequest(
        @NotNull(message = "Account ID is mandatory")
        UUID accountId,
        @NotNull(message = "Competence ID is mandatory")
        UUID competenceId,
        @NotNull(message = "Amount is mandatory")
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        String description
) {}
