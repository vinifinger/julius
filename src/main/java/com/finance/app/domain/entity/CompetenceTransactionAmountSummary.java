package com.finance.app.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record CompetenceTransactionAmountSummary(
        UUID competenceId,
        TransactionType type,
        TransactionStatus status,
        BigDecimal totalAmount) {
}
