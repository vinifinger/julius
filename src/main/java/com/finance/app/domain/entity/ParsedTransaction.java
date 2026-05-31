package com.finance.app.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ParsedTransaction(
        String externalId,
        String description,
        BigDecimal amount,
        LocalDateTime dateTime,
        TransactionType type
) {
}
