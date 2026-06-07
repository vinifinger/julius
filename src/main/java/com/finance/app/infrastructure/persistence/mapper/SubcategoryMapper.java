package com.finance.app.infrastructure.persistence.mapper;

import com.finance.app.domain.entity.Subcategory;
import com.finance.app.infrastructure.persistence.entity.CategoryEntity;
import com.finance.app.infrastructure.persistence.entity.SubcategoryEntity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SubcategoryMapper {

    public static Subcategory toDomain(SubcategoryEntity entity) {
        if (entity == null) {
            return null;
        }
        return Subcategory.builder()
                .id(entity.getId())
                .categoryId(entity.getCategory().getId())
                .name(entity.getName())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public static SubcategoryEntity toEntity(Subcategory domain) {
        if (domain == null) {
            return null;
        }
        return SubcategoryEntity.builder()
                .id(domain.getId())
                .category(CategoryEntity.builder().id(domain.getCategoryId()).build())
                .name(domain.getName())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

}
