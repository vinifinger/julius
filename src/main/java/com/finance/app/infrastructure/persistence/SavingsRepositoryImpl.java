package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.Savings;
import com.finance.app.domain.repository.SavingsRepository;
import com.finance.app.infrastructure.persistence.entity.SavingsEntity;
import com.finance.app.infrastructure.persistence.mapper.SavingsMapper;
import com.finance.app.infrastructure.persistence.repository.SavingsJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class SavingsRepositoryImpl implements SavingsRepository {

    private final SavingsJpaRepository jpaRepository;
    private final SavingsMapper mapper;

    @Override
    public Savings save(Savings savings) {
        SavingsEntity entity = mapper.toEntity(savings);
        SavingsEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Savings> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Savings> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
}
