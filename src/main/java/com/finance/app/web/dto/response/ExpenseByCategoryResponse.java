package com.finance.app.web.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ExpenseByCategoryResponse(
        String categoryName,
        String colorHex,
        BigDecimal totalAmount,
        BigDecimal percentage) {
}
