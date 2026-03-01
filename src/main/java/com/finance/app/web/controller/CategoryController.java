package com.finance.app.web.controller;

import com.finance.app.application.usecase.CategoryUseCase;
import com.finance.app.domain.port.UserContext;
import com.finance.app.web.dto.request.CreateCategoryRequest;
import com.finance.app.web.dto.response.CategoryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryUseCase categoryUseCase;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CreateCategoryRequest request) {
        UUID userId = userContext.getAuthenticatedUserId();
        CategoryResponse response = categoryUseCase.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> listByUser() {
        UUID userId = userContext.getAuthenticatedUserId();
        List<CategoryResponse> responses = categoryUseCase.listByUser(userId);
        return ResponseEntity.ok(responses);
    }

}
