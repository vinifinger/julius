package com.finance.app.web.dto.request;

import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionSubtype;
import com.finance.app.domain.entity.TransactionType;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record UpdateTransactionRequest(
        UUID accountId,
        UUID categoryId,
        UUID subcategoryId,
        UUID competenceId,
        String description,
        @Positive(message = "Amount must be positive")
        BigDecimal amount,
        LocalDateTime dateTime,
        TransactionType type,
        TransactionSubtype subtype,
        TransactionStatus status
) {
}
