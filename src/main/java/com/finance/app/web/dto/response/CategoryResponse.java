package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.Category;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record CategoryResponse(
        UUID id,
        String name,
        String colorHex,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static CategoryResponse fromDomain(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .colorHex(category.getColorHex())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}