package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Category;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.web.dto.request.CreateCategoryRequest;
import com.finance.app.web.dto.response.CategoryResponse;
import com.finance.app.domain.entity.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryUseCaseTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private com.finance.app.domain.repository.SubcategoryRepository subcategoryRepository;

    @InjectMocks
    private CategoryUseCase categoryUseCase;

    private final UUID userId = UUID.randomUUID();

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create category with color hex")
        void givenValidRequest_whenCreate_thenReturnsCategoryResponse() {
            // Given
            CreateCategoryRequest request = new CreateCategoryRequest("Alimentação", "#FF5733", TransactionType.EXPENSE);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            CategoryResponse response = categoryUseCase.create(request, userId);

            // Then
            assertNotNull(response);
            assertEquals("Alimentação", response.name());
            assertEquals("#FF5733", response.colorHex());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should create category without color hex")
        void givenNullColorHex_whenCreate_thenCreatesWithNullColor() {
            // Given
            CreateCategoryRequest request = new CreateCategoryRequest("Transporte", null, TransactionType.EXPENSE);
            when(categoryRepository.save(any(Category.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            CategoryResponse response = categoryUseCase.create(request, userId);

            // Then
            assertNotNull(response);
            assertEquals("Transporte", response.name());
        }
    }

    @Nested
    @DisplayName("listByUser")
    class ListByUser {

        @Test
        @DisplayName("Should return list of categories for user")
        void givenUserId_whenListByUser_thenReturnsCategories() {
            // Given
            Category category = Category.builder()
                    .id(UUID.randomUUID())
                    .userId(userId)
                    .name("Lazer")
                    .colorHex("#00FF00")
                    .type(TransactionType.EXPENSE)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            when(categoryRepository.findByUserId(userId)).thenReturn(List.of(category));

            // When
            List<CategoryResponse> responses = categoryUseCase.listByUser(userId, null);

            // Then
            assertEquals(1, responses.size());
            assertEquals("Lazer", responses.get(0).name());
        }

        @Test
        @DisplayName("Should return empty list when no categories")
        void givenNoCategories_whenListByUser_thenReturnsEmpty() {
            // Given
            when(categoryRepository.findByUserId(userId)).thenReturn(List.of());

            // When
            List<CategoryResponse> responses = categoryUseCase.listByUser(userId, null);

            // Then
            assertTrue(responses.isEmpty());
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Should update category successfully")
        void givenValidRequest_whenUpdate_thenReturnsUpdatedCategory() {
            UUID categoryId = UUID.randomUUID();
            Category existingCategory = Category.builder()
                    .id(categoryId)
                    .userId(userId)
                    .name("Old Name")
                    .colorHex("#000000")
                    .type(TransactionType.EXPENSE)
                    .build();

            when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.of(existingCategory));
            when(categoryRepository.save(any(Category.class))).thenAnswer(i -> i.getArgument(0));

            com.finance.app.web.dto.request.UpdateCategoryRequest request = new com.finance.app.web.dto.request.UpdateCategoryRequest("New Name", "#FFFFFF", TransactionType.EXPENSE);

            CategoryResponse response = categoryUseCase.update(categoryId, request);

            assertNotNull(response);
            assertEquals("New Name", response.name());
            assertEquals("#FFFFFF", response.colorHex());
            verify(categoryRepository).save(any(Category.class));
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when category does not exist")
        void givenNonExistingCategory_whenUpdate_thenThrowsException() {
            UUID categoryId = UUID.randomUUID();
            when(categoryRepository.findById(categoryId)).thenReturn(java.util.Optional.empty());

            com.finance.app.web.dto.request.UpdateCategoryRequest request = new com.finance.app.web.dto.request.UpdateCategoryRequest("New Name", "#FFFFFF", TransactionType.EXPENSE);

            org.junit.jupiter.api.Assertions.assertThrows(com.finance.app.domain.exception.CategoryNotFoundException.class, 
                () -> categoryUseCase.update(categoryId, request));
            
            verify(categoryRepository, org.mockito.Mockito.never()).save(any(Category.class));
        }
    }

}
