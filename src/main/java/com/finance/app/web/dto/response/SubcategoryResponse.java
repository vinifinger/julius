package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.Subcategory;
import java.time.LocalDateTime;
import java.util.UUID;

public record SubcategoryResponse(
        UUID id,
        UUID categoryId,
        String name,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static SubcategoryResponse fromDomain(Subcategory subcategory) {
        if (subcategory == null) {
            return null;
        }
        return new SubcategoryResponse(
                subcategory.getId(),
                subcategory.getCategoryId(),
                subcategory.getName(),
                subcategory.getCreatedAt(),
                subcategory.getUpdatedAt()
        );
    }
}
