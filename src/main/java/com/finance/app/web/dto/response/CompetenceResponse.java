package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.Competence;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CompetenceResponse(
        UUID id,
        String name,
        Integer month,
        Integer year,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static CompetenceResponse fromDomain(Competence competence) {
        String name = String.format("%02d/%d", competence.getMonth(), competence.getYear());
        return CompetenceResponse.builder()
                .id(competence.getId())
                .name(name)
                .month(competence.getMonth())
                .year(competence.getYear())
                .createdAt(competence.getCreatedAt())
                .updatedAt(competence.getUpdatedAt())
                .build();
    }

}
