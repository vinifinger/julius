package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.SavingsHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingsHistoryJpaRepository extends JpaRepository<SavingsHistoryEntity, UUID> {
    
    List<SavingsHistoryEntity> findBySavingsIdOrderByCreatedAtDesc(UUID savingsId);
    
}
