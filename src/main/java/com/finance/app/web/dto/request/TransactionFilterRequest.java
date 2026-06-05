package com.finance.app.web.dto.request;

import com.finance.app.domain.entity.TransactionFilter;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionSubtype;
import com.finance.app.domain.entity.TransactionType;

import java.time.LocalDate;
import java.util.UUID;

public record TransactionFilterRequest(
        UUID competenceId,
        TransactionStatus status,
        TransactionType type,
        TransactionSubtype subtype,
        UUID categoryId,
        UUID accountId,
        String description,
        LocalDate startDate,
        LocalDate endDate) {

    public TransactionFilter toDomain(UUID userId) {
        return TransactionFilter.builder()
                .competenceId(competenceId)
                .status(status)
                .type(type)
                .subtype(subtype)
                .userId(userId)
                .categoryId(categoryId)
                .accountId(accountId)
                .description(description)
                .startDate(startDate)
                .endDate(endDate)
                .build();
    }
}
