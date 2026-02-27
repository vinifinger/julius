package com.finance.app.infrastructure.persistence.mapper;

import com.finance.app.domain.entity.Competence;
import com.finance.app.infrastructure.persistence.entity.CompetenceEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class CompetenceMapper {

    public Competence toDomain(CompetenceEntity entity) {
        return Competence.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .month(entity.getMonth())
                .year(entity.getYear())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CompetenceEntity toEntity(Competence competence, UserEntity user) {
        return CompetenceEntity.builder()
                .id(competence.getId())
                .user(user)
                .month(competence.getMonth())
                .year(competence.getYear())
                .createdAt(competence.getCreatedAt())
                .updatedAt(competence.getUpdatedAt())
                .build();
    }

}
