package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.InstallmentSeries;

import java.math.BigDecimal;
import java.util.UUID;

public record InstallmentSeriesResponse(
        UUID parentId,
        String description,
        BigDecimal totalAmount,
        int totalInstallments,
        int completedInstallments,
        int pendingInstallments,
        BigDecimal completedAmount,
        BigDecimal pendingAmount
) {
    public static InstallmentSeriesResponse fromDomain(InstallmentSeries series) {
        if (series == null) return null;
        return new InstallmentSeriesResponse(
                series.parentId(),
                series.description(),
                series.totalAmount(),
                series.totalInstallments(),
                series.completedInstallments(),
                series.pendingInstallments(),
                series.completedAmount(),
                series.pendingAmount()
        );
    }
}
