package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionResponse(
        UUID id,
        UUID accountId,
        UUID categoryId,
        UUID competenceId,
        UUID userId,
        UUID parentId,
        Integer installmentCount,
        Integer installmentNumber,
        String description,
        BigDecimal amount,
        LocalDateTime dateTime,
        String type,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static TransactionResponse fromDomain(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getCompetenceId(),
                transaction.getUserId(),
                transaction.getParentId(),
                transaction.getInstallmentCount(),
                transaction.getInstallmentNumber(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getDateTime(),
                transaction.getType().name(),
                transaction.getStatus().name(),
                transaction.getCreatedAt(),
                transaction.getUpdatedAt());
    }

}
