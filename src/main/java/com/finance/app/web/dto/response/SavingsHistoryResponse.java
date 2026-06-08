package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.SavingsHistory;
import com.finance.app.domain.entity.SavingsHistoryType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record SavingsHistoryResponse(
        UUID id,
        UUID savingsId,
        UUID accountId,
        SavingsHistoryType type,
        BigDecimal amount,
        String description,
        LocalDateTime createdAt
) {
    public static SavingsHistoryResponse fromDomain(SavingsHistory history) {
        if (history == null) return null;
        return new SavingsHistoryResponse(
                history.getId(),
                history.getSavingsId(),
                history.getAccountId(),
                history.getType(),
                history.getAmount(),
                history.getDescription(),
                history.getCreatedAt()
        );
    }
}
