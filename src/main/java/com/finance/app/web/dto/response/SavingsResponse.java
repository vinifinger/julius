package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.Savings;

import java.math.BigDecimal;
import java.util.UUID;

public record SavingsResponse(
        UUID id,
        String name,
        BigDecimal balance,
        String colorHex,
        String icon
) {
    public static SavingsResponse fromDomain(Savings savings) {
        if (savings == null) return null;
        return new SavingsResponse(
                savings.getId(),
                savings.getName(),
                savings.getBalance(),
                savings.getColorHex(),
                savings.getIcon()
        );
    }
}
