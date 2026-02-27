package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.infrastructure.persistence.entity.AccountEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import com.finance.app.infrastructure.persistence.mapper.AccountMapper;
import com.finance.app.infrastructure.persistence.repository.AccountJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final AccountJpaRepository jpaRepository;
    private final AccountMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Optional<Account> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Account> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Account save(Account account) {
        UserEntity user = entityManager.getReference(UserEntity.class, account.getUserId());
        AccountEntity entity = mapper.toEntity(account, user);
        AccountEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

}
