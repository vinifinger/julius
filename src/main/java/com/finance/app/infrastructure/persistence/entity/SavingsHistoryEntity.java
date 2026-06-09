package com.finance.app.infrastructure.persistence.entity;

import com.finance.app.domain.entity.SavingsHistoryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "savings_history")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "savings_id", nullable = false)
    private SavingsEntity savings;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private AccountEntity account;

    @Column(name = "transaction_id")
    private UUID transactionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "`type`", nullable = false)
    private SavingsHistoryType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

}
