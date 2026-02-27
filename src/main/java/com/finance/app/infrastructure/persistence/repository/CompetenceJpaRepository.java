package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.CompetenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompetenceJpaRepository extends JpaRepository<CompetenceEntity, UUID> {

    List<CompetenceEntity> findByUserId(UUID userId);

    Optional<CompetenceEntity> findByUserIdAndMonthAndYear(UUID userId, Integer month, Integer year);

}
