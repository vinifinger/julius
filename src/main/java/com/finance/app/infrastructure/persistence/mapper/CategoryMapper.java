package com.finance.app.infrastructure.persistence.mapper;

import com.finance.app.domain.entity.Category;
import com.finance.app.infrastructure.persistence.entity.CategoryEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public Category toDomain(CategoryEntity entity) {
        return Category.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .colorHex(entity.getColorHex())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CategoryEntity toEntity(Category category, UserEntity user) {
        return CategoryEntity.builder()
                .id(category.getId())
                .user(user)
                .name(category.getName())
                .colorHex(category.getColorHex())
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

}
