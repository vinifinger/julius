package com.finance.app.domain.entity;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private UUID id;
    private UUID userId;
    private String name;
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void updateBalance(BigDecimal amount, TransactionType type) {
        if (TransactionType.EXPENSE.equals(type)) {
            this.balance = this.balance.subtract(amount).setScale(2, RoundingMode.HALF_EVEN);
        } else {
            this.balance = this.balance.add(amount).setScale(2, RoundingMode.HALF_EVEN);
        }
    }

}
