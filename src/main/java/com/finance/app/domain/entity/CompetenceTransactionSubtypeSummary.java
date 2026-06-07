package com.finance.app.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record CompetenceTransactionSubtypeSummary(
        UUID competenceId,
        TransactionType type,
        TransactionStatus status,
        TransactionSubtype subtype,
        Long transactionCount,
        BigDecimal totalAmount) {
}
