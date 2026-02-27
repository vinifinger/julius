package com.finance.app.domain.entity;

import java.math.BigDecimal;

public record CategoryExpenseSummary(
        String categoryName,
        String colorHex,
        BigDecimal totalAmount) {
}
