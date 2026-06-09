package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Category;
import com.finance.app.domain.entity.Subcategory;
import com.finance.app.domain.exception.CategoryNotFoundException;
import com.finance.app.domain.exception.SubcategoryNotFoundException;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.SubcategoryRepository;
import com.finance.app.web.dto.request.CreateSubcategoryRequest;
import com.finance.app.web.dto.request.UpdateSubcategoryRequest;
import com.finance.app.web.dto.response.SubcategoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SubcategoryUseCase {

    private final SubcategoryRepository subcategoryRepository;
    private final CategoryRepository categoryRepository;

    public SubcategoryResponse create(UUID categoryId, CreateSubcategoryRequest request, UUID userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        if (!category.getUserId().equals(userId)) {
            throw new CategoryNotFoundException(categoryId);
        }

        LocalDateTime now = LocalDateTime.now();
        Subcategory subcategory = Subcategory.builder()
                .categoryId(category.getId())
                .name(request.name())
                .createdAt(now)
                .updatedAt(now)
                .build();

        Subcategory savedSubcategory = subcategoryRepository.save(subcategory);

        return new SubcategoryResponse(
                savedSubcategory.getId(),
                savedSubcategory.getCategoryId(),
                savedSubcategory.getName(),
                savedSubcategory.getCreatedAt(),
                savedSubcategory.getUpdatedAt()
        );
    }

    public SubcategoryResponse update(UUID id, UpdateSubcategoryRequest request, UUID userId) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new SubcategoryNotFoundException(id));

        Category category = categoryRepository.findById(subcategory.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(subcategory.getCategoryId()));

        if (!category.getUserId().equals(userId)) {
            throw new SubcategoryNotFoundException(id);
        }

        subcategory.setName(request.name());
        subcategory.setUpdatedAt(LocalDateTime.now());

        Subcategory updatedSubcategory = subcategoryRepository.save(subcategory);

        return new SubcategoryResponse(
                updatedSubcategory.getId(),
                updatedSubcategory.getCategoryId(),
                updatedSubcategory.getName(),
                updatedSubcategory.getCreatedAt(),
                updatedSubcategory.getUpdatedAt()
        );
    }

    public void delete(UUID id, UUID userId) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new SubcategoryNotFoundException(id));

        Category category = categoryRepository.findById(subcategory.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(subcategory.getCategoryId()));

        if (!category.getUserId().equals(userId)) {
            throw new SubcategoryNotFoundException(id);
        }

        subcategoryRepository.delete(id);
    }

}
