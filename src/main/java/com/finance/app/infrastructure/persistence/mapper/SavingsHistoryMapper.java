package com.finance.app.infrastructure.persistence.mapper;

import com.finance.app.domain.entity.SavingsHistory;
import com.finance.app.infrastructure.persistence.entity.SavingsHistoryEntity;
import com.finance.app.infrastructure.persistence.entity.SavingsEntity;
import com.finance.app.infrastructure.persistence.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class SavingsHistoryMapper {

    public SavingsHistory toDomain(SavingsHistoryEntity entity) {
        if (entity == null) {
            return null;
        }

        return SavingsHistory.builder()
                .id(entity.getId())
                .savingsId(entity.getSavings().getId())
                .accountId(entity.getAccount().getId())
                .type(entity.getType())
                .amount(entity.getAmount())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public SavingsHistoryEntity toEntity(SavingsHistory domain) {
        if (domain == null) {
            return null;
        }

        SavingsEntity savingsEntity = SavingsEntity.builder().id(domain.getSavingsId()).build();
        AccountEntity accountEntity = AccountEntity.builder().id(domain.getAccountId()).build();

        return SavingsHistoryEntity.builder()
                .id(domain.getId())
                .savings(savingsEntity)
                .account(accountEntity)
                .type(domain.getType())
                .amount(domain.getAmount())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
