package com.finance.app.domain.repository;

import com.finance.app.domain.entity.Transaction;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    Optional<Transaction> findById(UUID id);

    List<Transaction> findByUserId(UUID userId);

    List<Transaction> findByAccountId(UUID accountId);

    List<Transaction> findByCompetenceId(UUID competenceId);

    Transaction save(Transaction transaction);

    void delete(UUID id);

}
