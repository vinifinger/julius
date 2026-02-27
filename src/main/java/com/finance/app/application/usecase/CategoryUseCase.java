package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Category;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.web.dto.request.CreateCategoryRequest;
import com.finance.app.web.dto.response.CategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryUseCase {

    private final CategoryRepository categoryRepository;

    public CategoryResponse create(CreateCategoryRequest request, UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        Category category = Category.builder()
                .userId(userId)
                .name(request.name())
                .colorHex(request.colorHex())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponse.fromDomain(savedCategory);
    }

    public List<CategoryResponse> listByUser(UUID userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(CategoryResponse::fromDomain)
                .toList();
    }

}
