package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.Category;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.finance.app.domain.entity.Subcategory;

@Builder
public record CategoryResponse(
        UUID id,
        String name,
        String colorHex,
        List<SubcategoryResponse> subcategories,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static CategoryResponse fromDomain(Category category) {
        return fromDomain(category, List.of());
    }

    public static CategoryResponse fromDomain(Category category, List<Subcategory> subcategories) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .colorHex(category.getColorHex())
                .subcategories(subcategories != null ? subcategories.stream().map(SubcategoryResponse::fromDomain).toList() : List.of())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }
}