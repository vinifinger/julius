package com.finance.app.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsHistory {

    private UUID id;
    private UUID savingsId;
    private UUID accountId;
    private SavingsHistoryType type;
    private BigDecimal amount;
    private String description;
    private LocalDateTime createdAt;

}
