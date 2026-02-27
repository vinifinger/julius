package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.Account;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record AccountResponse(
        UUID id,
        String name,
        BigDecimal balance,
        String currency,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static AccountResponse fromDomain(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .name(account.getName())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

}
