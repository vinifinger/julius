package com.finance.app.web.dto.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record DashboardSummaryResponse(
        BigDecimal totalRevenue,
        BigDecimal totalExpenses,
        BigDecimal monthlyBalance,
        String status) {
}
