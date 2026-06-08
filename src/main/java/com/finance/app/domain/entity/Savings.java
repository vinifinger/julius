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
public class Savings {

    private UUID id;
    private UUID userId;
    private String name;
    private BigDecimal balance;
    private String colorHex;
    private String icon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
