package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.SavingsHistory;
import com.finance.app.domain.repository.SavingsHistoryRepository;
import com.finance.app.infrastructure.persistence.entity.SavingsHistoryEntity;
import com.finance.app.infrastructure.persistence.mapper.SavingsHistoryMapper;
import com.finance.app.infrastructure.persistence.repository.SavingsHistoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SavingsHistoryRepositoryImpl implements SavingsHistoryRepository {

    private final SavingsHistoryJpaRepository jpaRepository;
    private final SavingsHistoryMapper mapper;

    @Override
    public SavingsHistory save(SavingsHistory history) {
        SavingsHistoryEntity entity = mapper.toEntity(history);
        SavingsHistoryEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public List<SavingsHistory> findBySavingsIdOrderByCreatedAtDesc(UUID savingsId) {
        return jpaRepository.findBySavingsIdOrderByCreatedAtDesc(savingsId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}
