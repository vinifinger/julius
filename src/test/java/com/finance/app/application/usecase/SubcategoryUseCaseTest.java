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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubcategoryUseCaseTest {

    @Mock
    private SubcategoryRepository subcategoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private SubcategoryUseCase subcategoryUseCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID subcategoryId = UUID.randomUUID();

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create subcategory successfully")
        void givenValidRequest_whenCreate_thenReturnsSubcategoryResponse() {
            // Given
            CreateSubcategoryRequest request = new CreateSubcategoryRequest("Supermercado");
            Category category = Category.builder().id(categoryId).userId(userId).build();
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(subcategoryRepository.save(any(Subcategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SubcategoryResponse response = subcategoryUseCase.create(categoryId, request, userId);

            // Then
            assertNotNull(response);
            assertEquals("Supermercado", response.name());
            assertEquals(categoryId, response.categoryId());
            verify(subcategoryRepository).save(any(Subcategory.class));
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when category does not exist")
        void givenInvalidCategory_whenCreate_thenThrowsException() {
            CreateSubcategoryRequest request = new CreateSubcategoryRequest("Supermercado");
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

            assertThrows(CategoryNotFoundException.class, () -> subcategoryUseCase.create(categoryId, request, userId));
        }

        @Test
        @DisplayName("Should throw CategoryNotFoundException when category belongs to another user")
        void givenOtherUserCategory_whenCreate_thenThrowsException() {
            CreateSubcategoryRequest request = new CreateSubcategoryRequest("Supermercado");
            Category category = Category.builder().id(categoryId).userId(UUID.randomUUID()).build();
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            assertThrows(CategoryNotFoundException.class, () -> subcategoryUseCase.create(categoryId, request, userId));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("Should update subcategory successfully")
        void givenValidRequest_whenUpdate_thenReturnsSubcategoryResponse() {
            // Given
            UpdateSubcategoryRequest request = new UpdateSubcategoryRequest("Supermercado (Editado)");
            Subcategory subcategory = Subcategory.builder().id(subcategoryId).categoryId(categoryId).name("Old Name").build();
            Category category = Category.builder().id(categoryId).userId(userId).build();

            when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.of(subcategory));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
            when(subcategoryRepository.save(any(Subcategory.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SubcategoryResponse response = subcategoryUseCase.update(subcategoryId, request, userId);

            // Then
            assertNotNull(response);
            assertEquals("Supermercado (Editado)", response.name());
            verify(subcategoryRepository).save(any(Subcategory.class));
        }

        @Test
        @DisplayName("Should throw SubcategoryNotFoundException when subcategory does not exist")
        void givenInvalidSubcategory_whenUpdate_thenThrowsException() {
            UpdateSubcategoryRequest request = new UpdateSubcategoryRequest("Supermercado (Editado)");
            when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.empty());

            assertThrows(SubcategoryNotFoundException.class, () -> subcategoryUseCase.update(subcategoryId, request, userId));
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should delete subcategory successfully")
        void givenValidId_whenDelete_thenDeletesSubcategory() {
            // Given
            Subcategory subcategory = Subcategory.builder().id(subcategoryId).categoryId(categoryId).build();
            Category category = Category.builder().id(categoryId).userId(userId).build();

            when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.of(subcategory));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

            // When
            subcategoryUseCase.delete(subcategoryId, userId);

            // Then
            verify(subcategoryRepository).delete(subcategoryId);
        }

        @Test
        @DisplayName("Should throw SubcategoryNotFoundException when subcategory does not exist")
        void givenInvalidSubcategory_whenDelete_thenThrowsException() {
            when(subcategoryRepository.findById(subcategoryId)).thenReturn(Optional.empty());

            assertThrows(SubcategoryNotFoundException.class, () -> subcategoryUseCase.delete(subcategoryId, userId));
        }
    }
}
