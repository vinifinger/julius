package com.finance.app.domain.repository;

import com.finance.app.domain.entity.Competence;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompetenceRepository {

    Optional<Competence> findById(UUID id);

    List<Competence> findByUserId(UUID userId);

    List<Competence> findByUserIdOrderByYearDescMonthDesc(UUID userId);

    Optional<Competence> findByUserIdAndMonthAndYear(UUID userId, Integer month, Integer year);

    Competence save(Competence competence);

}
