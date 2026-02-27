package com.finance.app.application.usecase;

import com.finance.app.domain.entity.CategoryExpenseSummary;
import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.CompetenceAmountSummary;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.CompetenceNotFoundException;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.web.dto.response.DashboardSummaryResponse;
import com.finance.app.web.dto.response.ExpenseByCategoryResponse;
import com.finance.app.web.dto.response.MonthlyEvolutionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DashboardUseCaseTest {

        @Mock
        private TransactionRepository transactionRepository;

        @Mock
        private CompetenceRepository competenceRepository;

        @InjectMocks
        private DashboardUseCase dashboardUseCase;

        private final UUID userId = UUID.randomUUID();

        private Competence createCompetence(UUID id, int month, int year) {
                return Competence.builder()
                                .id(id)
                                .userId(userId)
                                .month(month)
                                .year(year)
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
        }

        @Nested
        @DisplayName("getSummary")
        class GetSummary {

                @Test
                @DisplayName("Should return positive summary when revenue exceeds expenses")
                void givenRevenueExceedsExpenses_whenGetSummary_thenReturnsPositive() {
                        // Given
                        UUID competenceId = UUID.randomUUID();
                        Competence competence = createCompetence(competenceId, 2, 2026);
                        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competence));
                        when(transactionRepository.sumAmountByCompetenceIdAndType(competenceId,
                                        TransactionType.REVENUE))
                                        .thenReturn(BigDecimal.valueOf(5000.00));
                        when(transactionRepository.sumAmountByCompetenceIdAndType(competenceId,
                                        TransactionType.EXPENSE))
                                        .thenReturn(BigDecimal.valueOf(3250.00));

                        // When
                        DashboardSummaryResponse response = dashboardUseCase.getSummary(competenceId);

                        // Then
                        assertEquals(BigDecimal.valueOf(5000.00).setScale(2), response.totalRevenue());
                        assertEquals(BigDecimal.valueOf(3250.00).setScale(2), response.totalExpenses());
                        assertEquals(BigDecimal.valueOf(1750.00).setScale(2), response.monthlyBalance());
                        assertEquals("POSITIVE", response.status());
                }

                @Test
                @DisplayName("Should return negative summary when expenses exceed revenue")
                void givenExpensesExceedRevenue_whenGetSummary_thenReturnsNegative() {
                        // Given
                        UUID competenceId = UUID.randomUUID();
                        Competence competence = createCompetence(competenceId, 2, 2026);
                        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competence));
                        when(transactionRepository.sumAmountByCompetenceIdAndType(competenceId,
                                        TransactionType.REVENUE))
                                        .thenReturn(BigDecimal.valueOf(1000.00));
                        when(transactionRepository.sumAmountByCompetenceIdAndType(competenceId,
                                        TransactionType.EXPENSE))
                                        .thenReturn(BigDecimal.valueOf(3000.00));

                        // When
                        DashboardSummaryResponse response = dashboardUseCase.getSummary(competenceId);

                        // Then
                        assertEquals(BigDecimal.valueOf(-2000.00).setScale(2), response.monthlyBalance());
                        assertEquals("NEGATIVE", response.status());
                }

                @Test
                @DisplayName("Should return neutral summary when revenue equals expenses")
                void givenBalance_whenGetSummary_thenReturnsNeutral() {
                        // Given
                        UUID competenceId = UUID.randomUUID();
                        Competence competence = createCompetence(competenceId, 2, 2026);
                        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competence));
                        when(transactionRepository.sumAmountByCompetenceIdAndType(competenceId,
                                        TransactionType.REVENUE))
                                        .thenReturn(BigDecimal.valueOf(2000.00));
                        when(transactionRepository.sumAmountByCompetenceIdAndType(competenceId,
                                        TransactionType.EXPENSE))
                                        .thenReturn(BigDecimal.valueOf(2000.00));

                        // When
                        DashboardSummaryResponse response = dashboardUseCase.getSummary(competenceId);

                        // Then
                        assertEquals(BigDecimal.ZERO.setScale(2), response.monthlyBalance());
                        assertEquals("NEUTRAL", response.status());
                }

                @Test
                @DisplayName("Should return zero values when no transactions")
                void givenNoTransactions_whenGetSummary_thenReturnsZeros() {
                        // Given
                        UUID competenceId = UUID.randomUUID();
                        Competence competence = createCompetence(competenceId, 2, 2026);
                        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competence));
                        when(transactionRepository.sumAmountByCompetenceIdAndType(competenceId,
                                        TransactionType.REVENUE))
                                        .thenReturn(BigDecimal.ZERO);
                        when(transactionRepository.sumAmountByCompetenceIdAndType(competenceId,
                                        TransactionType.EXPENSE))
                                        .thenReturn(BigDecimal.ZERO);

                        // When
                        DashboardSummaryResponse response = dashboardUseCase.getSummary(competenceId);

                        // Then
                        assertEquals(BigDecimal.ZERO.setScale(2), response.totalRevenue());
                        assertEquals(BigDecimal.ZERO.setScale(2), response.totalExpenses());
                        assertEquals(BigDecimal.ZERO.setScale(2), response.monthlyBalance());
                        assertEquals("NEUTRAL", response.status());
                }

                @Test
                @DisplayName("Should throw CompetenceNotFoundException for invalid competenceId")
                void givenInvalidCompetenceId_whenGetSummary_thenThrows() {
                        // Given
                        UUID competenceId = UUID.randomUUID();
                        when(competenceRepository.findById(competenceId)).thenReturn(Optional.empty());

                        // When / Then
                        assertThrows(CompetenceNotFoundException.class,
                                        () -> dashboardUseCase.getSummary(competenceId));
                }
        }

        @Nested
        @DisplayName("getExpensesByCategory")
        class GetExpensesByCategory {

                @Test
                @DisplayName("Should return expenses grouped by category with percentages")
                void givenExpenses_whenGetByCategory_thenReturnsGrouped() {
                        // Given
                        UUID competenceId = UUID.randomUUID();
                        Competence competence = createCompetence(competenceId, 2, 2026);
                        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competence));

                        List<CategoryExpenseSummary> rows = List.of(
                                        new CategoryExpenseSummary("Alimentação", "#FF5733",
                                                        BigDecimal.valueOf(1500.00)),
                                        new CategoryExpenseSummary("Transporte", "#33FF57",
                                                        BigDecimal.valueOf(500.00)));
                        when(transactionRepository.sumExpensesByCategory(competenceId)).thenReturn(rows);

                        // When
                        List<ExpenseByCategoryResponse> responses = dashboardUseCase
                                        .getExpensesByCategory(competenceId);

                        // Then
                        assertEquals(2, responses.size());
                        assertEquals("Alimentação", responses.get(0).categoryName());
                        assertEquals("#FF5733", responses.get(0).colorHex());
                        assertEquals(BigDecimal.valueOf(1500.00).setScale(2), responses.get(0).totalAmount());
                        assertEquals(BigDecimal.valueOf(75.00).setScale(2), responses.get(0).percentage());
                        assertEquals(BigDecimal.valueOf(25.00).setScale(2), responses.get(1).percentage());
                }

                @Test
                @DisplayName("Should return empty list when no expenses")
                void givenNoExpenses_whenGetByCategory_thenReturnsEmpty() {
                        // Given
                        UUID competenceId = UUID.randomUUID();
                        Competence competence = createCompetence(competenceId, 2, 2026);
                        when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(competence));
                        when(transactionRepository.sumExpensesByCategory(competenceId))
                                        .thenReturn(Collections.emptyList());

                        // When
                        List<ExpenseByCategoryResponse> responses = dashboardUseCase
                                        .getExpensesByCategory(competenceId);

                        // Then
                        assertTrue(responses.isEmpty());
                }

                @Test
                @DisplayName("Should throw CompetenceNotFoundException for invalid competenceId")
                void givenInvalidCompetenceId_whenGetByCategory_thenThrows() {
                        // Given
                        UUID competenceId = UUID.randomUUID();
                        when(competenceRepository.findById(competenceId)).thenReturn(Optional.empty());

                        // When / Then
                        assertThrows(CompetenceNotFoundException.class,
                                        () -> dashboardUseCase.getExpensesByCategory(competenceId));
                }
        }

        @Nested
        @DisplayName("getEvolution")
        class GetEvolution {

                @Test
                @DisplayName("Should return last 6 months evolution with revenue, expenses and balance")
                void givenCompetences_whenGetEvolution_thenReturnsLast6() {
                        // Given
                        UUID comp1Id = UUID.randomUUID();
                        UUID comp2Id = UUID.randomUUID();
                        UUID comp3Id = UUID.randomUUID();

                        Competence comp1 = createCompetence(comp1Id, 2, 2026);
                        Competence comp2 = createCompetence(comp2Id, 1, 2026);
                        Competence comp3 = createCompetence(comp3Id, 12, 2025);

                        when(competenceRepository.findByUserIdOrderByYearDescMonthDesc(userId))
                                        .thenReturn(List.of(comp1, comp2, comp3));

                        List<CompetenceAmountSummary> aggregatedData = List.of(
                                        new CompetenceAmountSummary(comp1Id, TransactionType.REVENUE,
                                                        BigDecimal.valueOf(5000.00)),
                                        new CompetenceAmountSummary(comp1Id, TransactionType.EXPENSE,
                                                        BigDecimal.valueOf(3000.00)),
                                        new CompetenceAmountSummary(comp2Id, TransactionType.REVENUE,
                                                        BigDecimal.valueOf(4000.00)),
                                        new CompetenceAmountSummary(comp2Id, TransactionType.EXPENSE,
                                                        BigDecimal.valueOf(4500.00)),
                                        new CompetenceAmountSummary(comp3Id, TransactionType.REVENUE,
                                                        BigDecimal.valueOf(3000.00)));
                        when(transactionRepository.sumAmountByCompetenceIds(List.of(comp1Id, comp2Id, comp3Id)))
                                        .thenReturn(aggregatedData);

                        // When
                        List<MonthlyEvolutionResponse> responses = dashboardUseCase.getEvolution(userId);

                        // Then
                        assertEquals(3, responses.size());

                        assertEquals("02/2026", responses.get(0).competenceName());
                        assertEquals(BigDecimal.valueOf(5000.00).setScale(2), responses.get(0).totalRevenue());
                        assertEquals(BigDecimal.valueOf(3000.00).setScale(2), responses.get(0).totalExpenses());
                        assertEquals(BigDecimal.valueOf(2000.00).setScale(2), responses.get(0).balance());

                        assertEquals("01/2026", responses.get(1).competenceName());
                        assertEquals(BigDecimal.valueOf(-500.00).setScale(2), responses.get(1).balance());

                        assertEquals("12/2025", responses.get(2).competenceName());
                        assertEquals(BigDecimal.valueOf(3000.00).setScale(2), responses.get(2).totalRevenue());
                        assertEquals(BigDecimal.ZERO.setScale(2), responses.get(2).totalExpenses());
                        assertEquals(BigDecimal.valueOf(3000.00).setScale(2), responses.get(2).balance());
                }

                @Test
                @DisplayName("Should return empty list when no competences")
                void givenNoCompetences_whenGetEvolution_thenReturnsEmpty() {
                        // Given
                        when(competenceRepository.findByUserIdOrderByYearDescMonthDesc(userId))
                                        .thenReturn(Collections.emptyList());

                        // When
                        List<MonthlyEvolutionResponse> responses = dashboardUseCase.getEvolution(userId);

                        // Then
                        assertTrue(responses.isEmpty());
                }
        }

}
