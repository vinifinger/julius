package com.finance.app.web.controller;

import com.finance.app.application.usecase.InstallmentUseCase;
import com.finance.app.domain.entity.InstallmentSeries;
import com.finance.app.domain.port.UserContext;
import com.finance.app.web.dto.request.CreateInstallmentRequest;
import com.finance.app.web.dto.request.UpdateInstallmentRequest;
import com.finance.app.web.dto.response.InstallmentSeriesResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/installments")
@RequiredArgsConstructor
public class InstallmentController {

    private final InstallmentUseCase installmentUseCase;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<InstallmentSeriesResponse> create(@Valid @RequestBody CreateInstallmentRequest request) {
        UUID userId = userContext.getAuthenticatedUserId();
        InstallmentSeries series = installmentUseCase.createInstallmentSeries(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(InstallmentSeriesResponse.fromDomain(series));
    }

    @GetMapping("/{parentId}")
    public ResponseEntity<InstallmentSeriesResponse> getProgress(@PathVariable UUID parentId) {
        InstallmentSeries series = installmentUseCase.getInstallmentProgress(parentId);
        return ResponseEntity.ok(InstallmentSeriesResponse.fromDomain(series));
    }

    @PutMapping("/{parentId}")
    public ResponseEntity<InstallmentSeriesResponse> updateSeries(@PathVariable UUID parentId,
            @Valid @RequestBody UpdateInstallmentRequest request) {
        InstallmentSeries series = installmentUseCase.updateInstallmentSeries(parentId, request);
        return ResponseEntity.ok(InstallmentSeriesResponse.fromDomain(series));
    }

    @PatchMapping("/{parentId}/type")
    public ResponseEntity<InstallmentSeriesResponse> changeType(@PathVariable UUID parentId,
            @RequestParam String type) {
        InstallmentSeries series = installmentUseCase.changeInstallmentType(parentId, type);
        return ResponseEntity.ok(InstallmentSeriesResponse.fromDomain(series));
    }

}
