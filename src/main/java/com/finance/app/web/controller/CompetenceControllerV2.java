package com.finance.app.web.controller;

import com.finance.app.application.usecase.CompetenceUseCase;
import com.finance.app.domain.port.UserContext;
import com.finance.app.web.dto.response.CompetenceDetailResponseV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v2/competences")
@RequiredArgsConstructor
public class CompetenceControllerV2 {

    private final CompetenceUseCase competenceUseCase;
    private final UserContext userContext;

    @GetMapping("/{id}")
    public ResponseEntity<CompetenceDetailResponseV2> getById(@PathVariable UUID id) {
        UUID userId = userContext.getAuthenticatedUserId();
        CompetenceDetailResponseV2 response = competenceUseCase.getByIdV2(id, userId);
        return ResponseEntity.ok(response);
    }

}
