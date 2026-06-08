package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.SavingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SavingsJpaRepository extends JpaRepository<SavingsEntity, UUID> {
    
    List<SavingsEntity> findByUserId(UUID userId);
    
}
