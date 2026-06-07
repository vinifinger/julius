package com.finance.app.web.controller;

import com.finance.app.application.usecase.CompetenceUseCase;
import com.finance.app.domain.port.UserContext;
import com.finance.app.web.dto.response.CompetenceDetailResponseV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompetenceControllerV2Test {

    @Mock
    private CompetenceUseCase competenceUseCase;

    @Mock
    private UserContext userContext;

    @InjectMocks
    private CompetenceControllerV2 competenceController;

    private final UUID userId = UUID.randomUUID();

    @Test
    @DisplayName("Should return CompetenceDetailResponseV2 when calling getById")
    void givenValidId_whenGetById_thenReturnsOk() {
        // Given
        UUID compId = UUID.randomUUID();
        when(userContext.getAuthenticatedUserId()).thenReturn(userId);

        CompetenceDetailResponseV2.TotalSummaryDetail totalSummary = CompetenceDetailResponseV2.TotalSummaryDetail.builder()
                .totalRevenue(new BigDecimal("100.00"))
                .totalExpense(new BigDecimal("-50.00"))
                .totalBalance(new BigDecimal("50.00"))
                .build();

        CompetenceDetailResponseV2.SummaryDetail pendingSummary = CompetenceDetailResponseV2.SummaryDetail.builder()
                .transactionCount(2L)
                .totalSummary(totalSummary)
                .build();

        CompetenceDetailResponseV2 mockResponse = CompetenceDetailResponseV2.builder()
                .id(compId)
                .name("06/2026")
                .month(6)
                .year(2026)
                .pendingSummary(pendingSummary)
                .completedSummary(pendingSummary)
                .totalSummary(totalSummary)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(competenceUseCase.getByIdV2(compId, userId)).thenReturn(mockResponse);

        // When
        ResponseEntity<CompetenceDetailResponseV2> responseEntity = competenceController.getById(compId);

        // Then
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(mockResponse, responseEntity.getBody());
    }
}
