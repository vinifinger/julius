package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.infrastructure.persistence.entity.CompetenceEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import com.finance.app.infrastructure.persistence.mapper.CompetenceMapper;
import com.finance.app.infrastructure.persistence.repository.CompetenceJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompetenceRepositoryImpl implements CompetenceRepository {

    private final CompetenceJpaRepository jpaRepository;
    private final CompetenceMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Optional<Competence> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Competence> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Competence> findByUserIdOrderByYearDescMonthDesc(UUID userId) {
        return jpaRepository.findByUserIdOrderByYearDescMonthDesc(userId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Competence> findByUserIdAndMonthAndYear(UUID userId, Integer month, Integer year) {
        return jpaRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .map(mapper::toDomain);
    }

    @Override
    public Competence save(Competence competence) {
        UserEntity user = entityManager.getReference(UserEntity.class, competence.getUserId());
        CompetenceEntity entity = mapper.toEntity(competence, user);
        CompetenceEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

}
