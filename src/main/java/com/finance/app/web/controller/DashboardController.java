package com.finance.app.web.controller;

import com.finance.app.application.usecase.DashboardUseCase;
import com.finance.app.web.dto.response.DashboardSummaryResponse;
import com.finance.app.web.dto.response.ExpenseByCategoryResponse;
import com.finance.app.web.dto.response.MonthlyEvolutionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardUseCase dashboardUseCase;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(@RequestParam UUID competenceId) {
        DashboardSummaryResponse response = dashboardUseCase.getSummary(competenceId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/expenses-by-category")
    public ResponseEntity<List<ExpenseByCategoryResponse>> getExpensesByCategory(
            @RequestParam UUID competenceId) {
        List<ExpenseByCategoryResponse> responses = dashboardUseCase.getExpensesByCategory(competenceId);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/evolution")
    public ResponseEntity<List<MonthlyEvolutionResponse>> getEvolution(
            @RequestHeader("X-User-Id") UUID userId) {
        List<MonthlyEvolutionResponse> responses = dashboardUseCase.getEvolution(userId);
        return ResponseEntity.ok(responses);
    }

}
