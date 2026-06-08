package com.finance.app.domain.repository;

import com.finance.app.domain.entity.SavingsHistory;

import java.util.List;
import java.util.UUID;

public interface SavingsHistoryRepository {
    
    SavingsHistory save(SavingsHistory history);
    
    List<SavingsHistory> findBySavingsIdOrderByCreatedAtDesc(UUID savingsId);
    
}
