package com.finance.app.web.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CreateTransactionRequest(

        @NotNull(message = "Account ID is required") UUID accountId,

        @NotNull(message = "Category ID is required") UUID categoryId,

        @NotNull(message = "Competence ID is required") UUID competenceId,

        @NotNull(message = "User ID is required") UUID userId,

        @NotBlank(message = "Description is required") String description,

        @NotNull(message = "Amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal amount,

        @NotNull(message = "Date/time is required") LocalDateTime dateTime,

        @NotNull(message = "Type is required") String type,

        @NotNull(message = "Status is required") String status

) {
}
