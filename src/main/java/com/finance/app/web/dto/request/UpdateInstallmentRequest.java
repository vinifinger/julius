package com.finance.app.web.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateInstallmentRequest(
        @NotNull(message = "New total amount is required") @DecimalMin(value = "0.01", message = "Amount must be greater than zero") BigDecimal newTotalAmount
) {
}
