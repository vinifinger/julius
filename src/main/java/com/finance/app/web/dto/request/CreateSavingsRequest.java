package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

public record CreateSavingsRequest(
        @NotBlank(message = "Name is mandatory")
        String name,
        BigDecimal initialBalance,
        String colorHex,
        String icon
) {}
