package com.finance.app.web.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record MonthlyEvolutionResponse(
        Integer month,
        Integer year,
        String competenceName,
        BigDecimal totalRevenue,
        BigDecimal totalExpenses,
        BigDecimal balance) {
}
