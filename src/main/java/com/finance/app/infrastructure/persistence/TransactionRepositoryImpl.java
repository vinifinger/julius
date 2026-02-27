package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.CategoryExpenseSummary;
import com.finance.app.domain.entity.CompetenceAmountSummary;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.infrastructure.persistence.entity.AccountEntity;
import com.finance.app.infrastructure.persistence.entity.CategoryEntity;
import com.finance.app.infrastructure.persistence.entity.CompetenceEntity;
import com.finance.app.infrastructure.persistence.entity.TransactionEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import com.finance.app.infrastructure.persistence.mapper.TransactionMapper;
import com.finance.app.infrastructure.persistence.repository.TransactionJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository jpaRepository;
    private final TransactionMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Optional<Transaction> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByAccountId(UUID accountId) {
        return jpaRepository.findByAccountId(accountId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Transaction> findByCompetenceId(UUID competenceId) {
        return jpaRepository.findByCompetenceId(competenceId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Transaction save(Transaction transaction) {
        AccountEntity account = entityManager.getReference(AccountEntity.class, transaction.getAccountId());
        CategoryEntity category = entityManager.getReference(CategoryEntity.class, transaction.getCategoryId());
        CompetenceEntity competence = entityManager.getReference(CompetenceEntity.class, transaction.getCompetenceId());
        UserEntity user = entityManager.getReference(UserEntity.class, transaction.getUserId());
        TransactionEntity parent = Objects.nonNull(transaction.getParentId())
                ? entityManager.getReference(TransactionEntity.class, transaction.getParentId())
                : null;

        TransactionEntity entity = mapper.toEntity(transaction, account, category, competence, user, parent);
        TransactionEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public BigDecimal sumAmountByCompetenceIdAndType(UUID competenceId, TransactionType type) {
        return jpaRepository.sumAmountByCompetenceIdAndType(competenceId, type);
    }

    @Override
    public List<CategoryExpenseSummary> sumExpensesByCategory(UUID competenceId) {
        return jpaRepository.sumExpensesByCategory(competenceId);
    }

    @Override
    public List<CompetenceAmountSummary> sumAmountByCompetenceIds(List<UUID> competenceIds) {
        return jpaRepository.sumAmountByCompetenceIds(competenceIds);
    }

}
