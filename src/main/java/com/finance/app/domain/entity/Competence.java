package com.finance.app.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Competence {

    private UUID id;
    private UUID userId;
    private Integer month;
    private Integer year;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
