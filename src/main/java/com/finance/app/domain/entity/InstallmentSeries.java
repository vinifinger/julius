package com.finance.app.domain.entity;

import java.math.BigDecimal;
import java.util.List;
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
    public static InstallmentSeries fromTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return null;
        }

        Transaction first = transactions.get(0);
        UUID parentId = first.getParentId() != null ? first.getParentId() : first.getId();

        int totalInstallments = transactions.size();
        int paidCount = 0;
        int pendingCount = 0;
        BigDecimal paidSum = BigDecimal.ZERO;
        BigDecimal pendingSum = BigDecimal.ZERO;

        for (Transaction t : transactions) {
            if (t.isPaid()) {
                paidCount++;
                paidSum = paidSum.add(t.getAmount());
            } else {
                pendingCount++;
                pendingSum = pendingSum.add(t.getAmount());
            }
        }

        return new InstallmentSeries(
                parentId,
                first.getDescription(),
                paidSum.add(pendingSum),
                totalInstallments,
                paidCount,
                pendingCount,
                paidSum,
                pendingSum
        );
    }
}
