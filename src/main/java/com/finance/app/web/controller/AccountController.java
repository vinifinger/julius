package com.finance.app.web.controller;

import com.finance.app.application.usecase.AccountUseCase;
import com.finance.app.web.dto.request.CreateAccountRequest;
import com.finance.app.web.dto.response.AccountResponse;
import com.finance.app.web.dto.response.BalanceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountUseCase accountUseCase;

    @PostMapping
    public ResponseEntity<AccountResponse> create(@Valid @RequestBody CreateAccountRequest request,
            @RequestHeader("X-User-Id") UUID userId) {
        AccountResponse response = accountUseCase.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AccountResponse>> listByUser(@RequestHeader("X-User-Id") UUID userId) {
        List<AccountResponse> responses = accountUseCase.listByUser(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/balance")
    public ResponseEntity<AccountResponse> getBalance(@PathVariable UUID id) {
        AccountResponse response = accountUseCase.getBalance(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/total-balance")
    public ResponseEntity<BalanceResponse> getTotalBalance(@RequestHeader("X-User-Id") UUID userId) {
        BalanceResponse response = accountUseCase.getTotalBalance(userId);
        return ResponseEntity.ok(response);
    }

}
