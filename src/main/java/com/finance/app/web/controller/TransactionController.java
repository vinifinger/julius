package com.finance.app.web.controller;

import com.finance.app.application.usecase.TransactionUseCase;
import com.finance.app.domain.port.UserContext;
import com.finance.app.web.dto.request.CreateTransactionRequest;
import com.finance.app.web.dto.request.UpdateTransactionStatusRequest;
import com.finance.app.web.dto.response.TransactionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionUseCase transactionUseCase;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<TransactionResponse> create(@Valid @RequestBody CreateTransactionRequest request) {
        UUID userId = userContext.getAuthenticatedUserId();
        TransactionResponse response = transactionUseCase.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable UUID id) {
        TransactionResponse response = transactionUseCase.getById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<TransactionResponse>> listByUser() {
        UUID userId = userContext.getAuthenticatedUserId();
        List<TransactionResponse> responses = transactionUseCase.listByUser(userId);
        return ResponseEntity.ok(responses);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TransactionResponse> updateStatus(@PathVariable UUID id,
            @Valid @RequestBody UpdateTransactionStatusRequest request) {
        TransactionResponse response = transactionUseCase.updateStatus(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        transactionUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

}
