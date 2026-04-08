package com.finance.app.web.dto.request;

import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateInstallmentRequest(
        @NotNull(message = "Account ID is required") UUID accountId,
        @NotNull(message = "Category ID is required") UUID categoryId,
        @NotNull(message = "Competence ID is required") UUID competenceId,
        @NotBlank(message = "Description is required") String description,
        BigDecimal totalAmount,
        BigDecimal installmentAmount,
        @NotNull(message = "Number of installments is required") @Min(value = 2, message = "Installments must be at least 2") Integer installments,
        @NotNull(message = "Date/time is required") LocalDateTime dateTime,
        @NotNull(message = "Type is required") TransactionType type,
        @NotNull(message = "Status is required") TransactionStatus status
) {
}
