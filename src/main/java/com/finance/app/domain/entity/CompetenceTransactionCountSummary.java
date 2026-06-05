package com.finance.app.domain.entity;

import java.util.UUID;

public record CompetenceTransactionCountSummary(
        UUID competenceId,
        Long transactionCount) {
}
