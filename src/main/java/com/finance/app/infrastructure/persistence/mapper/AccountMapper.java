package com.finance.app.infrastructure.persistence.mapper;

import com.finance.app.domain.entity.Account;
import com.finance.app.infrastructure.persistence.entity.AccountEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public Account toDomain(AccountEntity entity) {
        return Account.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .balance(entity.getBalance())
                .currency(entity.getCurrency())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public AccountEntity toEntity(Account account, UserEntity user) {
        return AccountEntity.builder()
                .id(account.getId())
                .user(user)
                .name(account.getName())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }

}
