package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.Competence;

import lombok.Builder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CompetenceDetailResponse(
        UUID id,
        String name,
        Integer month,
        Integer year,
        Long transactionCount,
        BigDecimal paidAmount,
        BigDecimal pendingAmount,
        BigDecimal totalAmount,
        BigDecimal totalRevenue,
        BigDecimal totalExpense,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static CompetenceDetailResponse fromDomain(Competence competence, Long transactionCount,
            BigDecimal paidAmount, BigDecimal pendingAmount, BigDecimal totalAmount,
            BigDecimal totalRevenue, BigDecimal totalExpense) {
        String name = String.format("%02d/%d", competence.getMonth(), competence.getYear());
        return CompetenceDetailResponse.builder()
                .id(competence.getId())
                .name(name)
                .month(competence.getMonth())
                .year(competence.getYear())
                .transactionCount(transactionCount)
                .paidAmount(paidAmount.setScale(2, RoundingMode.HALF_EVEN))
                .pendingAmount(pendingAmount.setScale(2, RoundingMode.HALF_EVEN))
                .totalAmount(totalAmount.setScale(2, RoundingMode.HALF_EVEN))
                .totalRevenue(totalRevenue.setScale(2, RoundingMode.HALF_EVEN))
                .totalExpense(totalExpense.setScale(2, RoundingMode.HALF_EVEN))
                .createdAt(competence.getCreatedAt())
                .updatedAt(competence.getUpdatedAt())
                .build();
    }
}
