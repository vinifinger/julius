package com.finance.app.domain.repository;

import com.finance.app.domain.entity.Savings;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SavingsRepository {
    
    Savings save(Savings savings);
    
    Optional<Savings> findById(UUID id);
    
    List<Savings> findByUserId(UUID userId);
    
    void deleteById(UUID id);
    
}
