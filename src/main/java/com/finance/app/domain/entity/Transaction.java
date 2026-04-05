package com.finance.app.domain.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private UUID id;
    private UUID accountId;
    private UUID categoryId;
    private UUID competenceId;
    private UUID userId;
    private UUID parentId;
    private Integer installmentCount;
    private Integer installmentNumber;
    private String description;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private TransactionType type;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isPaid() {
        return TransactionStatus.PAID.equals(this.status);
    }

    public static Transaction create(UUID accountId,
            UUID categoryId,
            UUID competenceId,
            UUID userId,
            String description,
            BigDecimal amount,
            LocalDateTime dateTime,
            TransactionType type,
            TransactionStatus status) {
        return create(accountId, categoryId, competenceId, userId,
                description, amount, dateTime, type, status, null, null, null);
    }

    public static Transaction create(UUID accountId,
            UUID categoryId,
            UUID competenceId,
            UUID userId,
            String description,
            BigDecimal amount,
            LocalDateTime dateTime,
            TransactionType type,
            TransactionStatus status,
            UUID parentId,
            Integer installmentCount,
            Integer installmentNumber) {
        LocalDateTime now = LocalDateTime.now();
        return Transaction.builder()
                .accountId(accountId)
                .categoryId(categoryId)
                .competenceId(competenceId)
                .userId(userId)
                .parentId(parentId)
                .description(description)
                .amount(amount.setScale(2, RoundingMode.HALF_EVEN))
                .dateTime(dateTime)
                .type(type)
                .status(status)
                .installmentCount(installmentCount)
                .installmentNumber(installmentNumber)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

}
