package com.finance.app.web.controller;

import com.finance.app.application.usecase.CompetenceUseCase;
import com.finance.app.domain.port.UserContext;
import com.finance.app.web.dto.request.CreateCompetenceRequest;
import com.finance.app.web.dto.response.CompetenceResponse;
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
@RequestMapping("/api/v1/competences")
@RequiredArgsConstructor
public class CompetenceController {

    private final CompetenceUseCase competenceUseCase;
    private final UserContext userContext;

    @PostMapping
    public ResponseEntity<CompetenceResponse> create(@Valid @RequestBody CreateCompetenceRequest request) {
        UUID userId = userContext.getAuthenticatedUserId();
        CompetenceResponse response = competenceUseCase.create(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CompetenceResponse>> listAll() {
        UUID userId = userContext.getAuthenticatedUserId();
        List<CompetenceResponse> responses = competenceUseCase.listAll(userId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/current")
    public ResponseEntity<CompetenceResponse> getCurrent() {
        UUID userId = userContext.getAuthenticatedUserId();
        CompetenceResponse response = competenceUseCase.getCurrent(userId);
        return ResponseEntity.ok(response);
    }

}
