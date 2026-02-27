package com.finance.app.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record CompetenceAmountSummary(
        UUID competenceId,
        TransactionType type,
        BigDecimal totalAmount) {
}
