package com.finance.app.web.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CompetenceDetailResponseV2(
        UUID id,
        String name,
        Integer month,
        Integer year,
        SummaryDetail pendingSummary,
        SummaryDetail completedSummary,
        TotalSummaryDetail totalSummary,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    @Builder
    public record SummaryDetail(
            Long transactionCount,
            AmountDetail fixed,
            AmountDetail variable,
            TotalSummaryDetail totalSummary) {
    }

    @Builder
    public record AmountDetail(
            BigDecimal revenue,
            BigDecimal expense,
            BigDecimal balance) {
    }

    @Builder
    public record TotalSummaryDetail(
            BigDecimal totalRevenue,
            BigDecimal totalExpense,
            BigDecimal totalBalance) {
    }
}
