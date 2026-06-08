package com.finance.app.infrastructure.persistence.mapper;

import com.finance.app.domain.entity.Savings;
import com.finance.app.infrastructure.persistence.entity.SavingsEntity;
import org.springframework.stereotype.Component;

@Component
public class SavingsMapper {

    public Savings toDomain(SavingsEntity entity) {
        if (entity == null) {
            return null;
        }

        return Savings.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .name(entity.getName())
                .balance(entity.getBalance())
                .colorHex(entity.getColorHex())
                .icon(entity.getIcon())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public SavingsEntity toEntity(Savings domain) {
        if (domain == null) {
            return null;
        }

        return SavingsEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .name(domain.getName())
                .balance(domain.getBalance())
                .colorHex(domain.getColorHex())
                .icon(domain.getIcon())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
