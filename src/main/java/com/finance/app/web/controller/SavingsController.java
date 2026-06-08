package com.finance.app.web.controller;

import com.finance.app.application.usecase.SavingsUseCase;
import com.finance.app.web.dto.request.CreateSavingsRequest;
import com.finance.app.web.dto.request.SavingsTransactionRequest;
import com.finance.app.web.dto.request.UpdateSavingsRequest;
import com.finance.app.web.dto.response.SavingsHistoryResponse;
import com.finance.app.web.dto.response.SavingsResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/savings")
@RequiredArgsConstructor
public class SavingsController {

    private final SavingsUseCase savingsUseCase;

    @PostMapping
    public ResponseEntity<SavingsResponse> create(
            @Valid @RequestBody CreateSavingsRequest request,
            @RequestHeader("X-User-ID") UUID userId) {
        SavingsResponse response = savingsUseCase.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<SavingsResponse>> listByUser(
            @RequestHeader("X-User-ID") UUID userId) {
        List<SavingsResponse> response = savingsUseCase.listByUser(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SavingsResponse> getById(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {
        SavingsResponse response = savingsUseCase.getById(id, userId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SavingsResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateSavingsRequest request,
            @RequestHeader("X-User-ID") UUID userId) {
        SavingsResponse response = savingsUseCase.update(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {
        savingsUseCase.delete(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/deposit")
    public ResponseEntity<SavingsResponse> deposit(
            @PathVariable UUID id,
            @Valid @RequestBody SavingsTransactionRequest request,
            @RequestHeader("X-User-ID") UUID userId) {
        SavingsResponse response = savingsUseCase.deposit(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/withdraw")
    public ResponseEntity<SavingsResponse> withdraw(
            @PathVariable UUID id,
            @Valid @RequestBody SavingsTransactionRequest request,
            @RequestHeader("X-User-ID") UUID userId) {
        SavingsResponse response = savingsUseCase.withdraw(id, request, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/history")
    public ResponseEntity<List<SavingsHistoryResponse>> getHistory(
            @PathVariable UUID id,
            @RequestHeader("X-User-ID") UUID userId) {
        List<SavingsHistoryResponse> response = savingsUseCase.getHistory(id, userId);
        return ResponseEntity.ok(response);
    }
}
