package com.finance.app.web.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(

                @NotBlank(message = "Account name is required") String name,

                @NotNull(message = "Initial balance is required") @DecimalMin(value = "0.00", message = "Balance must be zero or positive") BigDecimal balance,

                String currency

) {
}
