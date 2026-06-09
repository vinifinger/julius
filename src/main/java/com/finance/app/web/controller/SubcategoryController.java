package com.finance.app.web.controller;

import com.finance.app.application.usecase.SubcategoryUseCase;
import com.finance.app.domain.port.UserContext;
import com.finance.app.web.dto.request.CreateSubcategoryRequest;
import com.finance.app.web.dto.request.UpdateSubcategoryRequest;
import com.finance.app.web.dto.response.SubcategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class SubcategoryController {

    private final SubcategoryUseCase subcategoryUseCase;
    private final UserContext userContext;

    @PostMapping("/categories/{categoryId}/subcategories")
    public ResponseEntity<SubcategoryResponse> create(
            @PathVariable UUID categoryId,
            @Valid @RequestBody CreateSubcategoryRequest request) {
        UUID userId = userContext.getAuthenticatedUserId();
        SubcategoryResponse response = subcategoryUseCase.create(categoryId, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/subcategories/{id}")
    public ResponseEntity<SubcategoryResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSubcategoryRequest request) {
        UUID userId = userContext.getAuthenticatedUserId();
        SubcategoryResponse response = subcategoryUseCase.update(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/subcategories/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        UUID userId = userContext.getAuthenticatedUserId();
        subcategoryUseCase.delete(id, userId);
        return ResponseEntity.noContent().build();
    }
}
