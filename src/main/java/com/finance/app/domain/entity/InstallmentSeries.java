package com.finance.app.domain.entity;

import java.math.BigDecimal;
import java.util.UUID;

public record InstallmentSeries(
        UUID parentId,
        String description,
        BigDecimal totalAmount,
        int totalInstallments,
        int paidInstallments,
        int pendingInstallments,
        BigDecimal paidAmount,
        BigDecimal pendingAmount
) {
}
