package com.finance.app.infrastructure.persistence.mapper;

import com.finance.app.domain.entity.Transaction;
import com.finance.app.infrastructure.persistence.entity.AccountEntity;
import com.finance.app.infrastructure.persistence.entity.CategoryEntity;
import com.finance.app.infrastructure.persistence.entity.CompetenceEntity;
import com.finance.app.infrastructure.persistence.entity.SubcategoryEntity;
import com.finance.app.infrastructure.persistence.entity.TransactionEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TransactionMapper {

    public Transaction toDomain(TransactionEntity entity) {
        return Transaction.builder()
                .id(entity.getId())
                .accountId(entity.getAccount().getId())
                .categoryId(entity.getCategory().getId())
                .subcategoryId(Objects.nonNull(entity.getSubcategory()) ? entity.getSubcategory().getId() : null)
                .competenceId(entity.getCompetence().getId())
                .userId(entity.getUser().getId())
                .parentId(Objects.nonNull(entity.getParent()) ? entity.getParent().getId() : null)
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .dateTime(entity.getDateTime())
                .type(entity.getType())
                .subtype(entity.getSubtype())
                .status(entity.getStatus())
                .installmentCount(entity.getInstallmentCount())
                .installmentNumber(entity.getInstallmentNumber())
                .externalId(entity.getExternalId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public TransactionEntity toEntity(Transaction transaction,
            AccountEntity account,
            CategoryEntity category,
            SubcategoryEntity subcategory,
            CompetenceEntity competence,
            UserEntity user,
            TransactionEntity parent) {
        return TransactionEntity.builder()
                .id(transaction.getId())
                .account(account)
                .category(category)
                .subcategory(subcategory)
                .competence(competence)
                .user(user)
                .parent(parent)
                .description(transaction.getDescription())
                .type(transaction.getType())
                .subtype(transaction.getSubtype())
                .amount(transaction.getAmount())
                .dateTime(transaction.getDateTime())
                .status(transaction.getStatus())
                .installmentCount(transaction.getInstallmentCount())
                .installmentNumber(transaction.getInstallmentNumber())
                .externalId(transaction.getExternalId())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }

}
