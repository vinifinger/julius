package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByUserId(UUID userId);

    List<TransactionEntity> findByAccountId(UUID accountId);

    List<TransactionEntity> findByCompetenceId(UUID competenceId);

}
