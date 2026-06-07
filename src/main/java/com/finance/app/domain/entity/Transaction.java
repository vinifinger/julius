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
    private UUID subcategoryId;
    private UUID competenceId;
    private UUID userId;
    private UUID parentId;
    private Integer installmentCount;
    private Integer installmentNumber;
    private String externalId;
    private String description;
    private BigDecimal amount;
    private LocalDateTime dateTime;
    private TransactionType type;
    private TransactionSubtype subtype;
    private TransactionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public boolean isCompleted() {
        return TransactionStatus.COMPLETED.equals(this.status);
    }

    public static Transaction create(UUID accountId,
            UUID categoryId,
            UUID subcategoryId,
            UUID competenceId,
            UUID userId,
            String description,
            BigDecimal amount,
            LocalDateTime dateTime,
            TransactionType type,
            TransactionSubtype subtype,
            TransactionStatus status) {
        return create(accountId, categoryId, subcategoryId, competenceId, userId,
                description, amount, dateTime, type, subtype, status, null, null, null, null);
    }

    public static Transaction create(UUID accountId,
            UUID categoryId,
            UUID subcategoryId,
            UUID competenceId,
            UUID userId,
            String description,
            BigDecimal amount,
            LocalDateTime dateTime,
            TransactionType type,
            TransactionSubtype subtype,
            TransactionStatus status,
            UUID parentId,
            Integer installmentCount,
            Integer installmentNumber,
            String externalId) {
        LocalDateTime now = LocalDateTime.now();
        return Transaction.builder()
                .accountId(accountId)
                .categoryId(categoryId)
                .subcategoryId(subcategoryId)
                .competenceId(competenceId)
                .userId(userId)
                .parentId(parentId)
                .description(description)
                .amount(amount.setScale(2, RoundingMode.HALF_EVEN))
                .dateTime(dateTime)
                .type(type)
                .subtype(subtype)
                .status(status)
                .installmentCount(installmentCount)
                .installmentNumber(installmentNumber)
                .externalId(externalId)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

}
