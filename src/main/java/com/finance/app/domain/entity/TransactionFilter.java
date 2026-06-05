package com.finance.app.domain.entity;

import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record TransactionFilter(
        UUID competenceId,
        TransactionStatus status,
        TransactionType type,
        TransactionSubtype subtype,
        UUID userId,
        UUID categoryId,
        UUID accountId,
        String description,
        LocalDate startDate,
        LocalDate endDate) {
}
