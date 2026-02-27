package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.web.dto.request.CreateCompetenceRequest;
import com.finance.app.web.dto.response.CompetenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompetenceUseCase {

    private final CompetenceRepository competenceRepository;

    public CompetenceResponse create(CreateCompetenceRequest request, UUID userId) {
        Optional<Competence> existing = competenceRepository.findByUserIdAndMonthAndYear(
                userId, request.month(), request.year());

        if (existing.isPresent()) {
            return CompetenceResponse.fromDomain(existing.get());
        }

        LocalDateTime now = LocalDateTime.now();
        Competence competence = Competence.builder()
                .userId(userId)
                .month(request.month())
                .year(request.year())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Competence savedCompetence = competenceRepository.save(competence);
        return CompetenceResponse.fromDomain(savedCompetence);
    }

    public List<CompetenceResponse> listAll(UUID userId) {
        return competenceRepository.findByUserIdOrderByYearDescMonthDesc(userId).stream()
                .map(CompetenceResponse::fromDomain)
                .toList();
    }

    public CompetenceResponse getCurrent(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        Integer currentMonth = now.getMonthValue();
        Integer currentYear = now.getYear();

        Optional<Competence> existing = competenceRepository.findByUserIdAndMonthAndYear(
                userId, currentMonth, currentYear);

        if (existing.isPresent()) {
            return CompetenceResponse.fromDomain(existing.get());
        }

        Competence competence = Competence.builder()
                .userId(userId)
                .month(currentMonth)
                .year(currentYear)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Competence savedCompetence = competenceRepository.save(competence);
        return CompetenceResponse.fromDomain(savedCompetence);
    }

}
