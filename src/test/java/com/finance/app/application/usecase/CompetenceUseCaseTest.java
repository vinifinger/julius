package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.CompetenceTransactionCountSummary;
import com.finance.app.domain.entity.CompetenceTransactionAmountSummary;
import com.finance.app.domain.entity.CompetenceTransactionSubtypeSummary;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionSubtype;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.web.dto.request.CreateCompetenceRequest;
import com.finance.app.web.dto.response.CompetenceResponse;
import com.finance.app.web.dto.response.CompetenceDetailResponse;
import com.finance.app.web.dto.response.CompetenceDetailResponseV2;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.finance.app.domain.exception.CompetenceNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompetenceUseCaseTest {

    @Mock
    private CompetenceRepository competenceRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private CompetenceUseCase competenceUseCase;

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
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create new competence when it does not exist")
        void givenNewCompetence_whenCreate_thenSavesAndReturns() {
            // Given
            CreateCompetenceRequest request = new CreateCompetenceRequest(2, 2026);
            when(competenceRepository.findByUserIdAndMonthAndYear(userId, 2, 2026))
                    .thenReturn(Optional.empty());
            when(competenceRepository.save(any(Competence.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(transactionRepository.countByCompetenceId(any()))
                    .thenReturn(5L);
            when(transactionRepository.sumAmountsByCompetenceId(any()))
                    .thenReturn(List.of(
                            new CompetenceTransactionAmountSummary(UUID.randomUUID(), TransactionType.REVENUE, TransactionStatus.COMPLETED, new BigDecimal("100.00")),
                            new CompetenceTransactionAmountSummary(UUID.randomUUID(), TransactionType.EXPENSE, TransactionStatus.COMPLETED, new BigDecimal("40.00")),
                            new CompetenceTransactionAmountSummary(UUID.randomUUID(), TransactionType.REVENUE, TransactionStatus.PENDING, new BigDecimal("50.00")),
                            new CompetenceTransactionAmountSummary(UUID.randomUUID(), TransactionType.EXPENSE, TransactionStatus.PENDING, new BigDecimal("10.00"))
                    ));

            // When
            CompetenceDetailResponse response = competenceUseCase.create(request, userId);

            // Then
            assertNotNull(response);
            assertEquals(2, response.month());
            assertEquals(2026, response.year());
            assertEquals("02/2026", response.name());
            assertEquals(5L, response.transactionCount());
            assertEquals(new BigDecimal("60.00"), response.completedAmount());
            assertEquals(new BigDecimal("40.00"), response.pendingAmount());
            assertEquals(new BigDecimal("100.00"), response.totalAmount());
            assertEquals(new BigDecimal("150.00"), response.totalRevenue());
            assertEquals(new BigDecimal("50.00"), response.totalExpense());
            verify(competenceRepository).save(any(Competence.class));
        }

        @Test
        @DisplayName("Should return existing competence when duplicate month/year")
        void givenExistingCompetence_whenCreate_thenReturnsExisting() {
            // Given
            UUID existingId = UUID.randomUUID();
            Competence existing = createCompetence(existingId, 2, 2026);
            CreateCompetenceRequest request = new CreateCompetenceRequest(2, 2026);
            when(competenceRepository.findByUserIdAndMonthAndYear(userId, 2, 2026))
                    .thenReturn(Optional.of(existing));
            when(transactionRepository.countByCompetenceId(existingId))
                    .thenReturn(10L);
            when(transactionRepository.sumAmountsByCompetenceId(existingId))
                    .thenReturn(List.of());

            // When
            CompetenceDetailResponse response = competenceUseCase.create(request, userId);

            // Then
            assertEquals(existingId, response.id());
            assertEquals("02/2026", response.name());
            assertEquals(10L, response.transactionCount());
            assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN), response.totalRevenue());
            assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN), response.totalExpense());
            verify(competenceRepository, never()).save(any(Competence.class));
        }

        @Test
        @DisplayName("Should generate correct name format for single-digit month")
        void givenSingleDigitMonth_whenCreate_thenFormatsNameWithLeadingZero() {
            // Given
            CreateCompetenceRequest request = new CreateCompetenceRequest(3, 2026);
            when(competenceRepository.findByUserIdAndMonthAndYear(userId, 3, 2026))
                    .thenReturn(Optional.empty());
            when(competenceRepository.save(any(Competence.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(transactionRepository.countByCompetenceId(any()))
                    .thenReturn(0L);
            when(transactionRepository.sumAmountsByCompetenceId(any()))
                    .thenReturn(List.of());

            // When
            CompetenceDetailResponse response = competenceUseCase.create(request, userId);

            // Then
            assertEquals("03/2026", response.name());
            assertEquals(0L, response.transactionCount());
        }

        @Test
        @DisplayName("Should generate correct name format for double-digit month")
        void givenDoubleDigitMonth_whenCreate_thenFormatsNameCorrectly() {
            // Given
            CreateCompetenceRequest request = new CreateCompetenceRequest(12, 2025);
            when(competenceRepository.findByUserIdAndMonthAndYear(userId, 12, 2025))
                    .thenReturn(Optional.empty());
            when(competenceRepository.save(any(Competence.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));
            when(transactionRepository.countByCompetenceId(any()))
                    .thenReturn(0L);
            when(transactionRepository.sumAmountsByCompetenceId(any()))
                    .thenReturn(List.of());

            // When
            CompetenceDetailResponse response = competenceUseCase.create(request, userId);

            // Then
            assertEquals("12/2025", response.name());
            assertEquals(0L, response.transactionCount());
        }
    }

    @Nested
    @DisplayName("getByIdV2")
    class GetByIdV2 {

        @Test
        @DisplayName("Should return CompetenceDetailResponseV2 when competence exists")
        void givenExistingCompetence_whenGetByIdV2_thenReturnsResponse() {
            // Given
            UUID compId = UUID.randomUUID();
            Competence competence = createCompetence(compId, 6, 2026);
            when(competenceRepository.findById(compId)).thenReturn(Optional.of(competence));

            when(transactionRepository.sumSubtypeAmountsByCompetenceId(compId)).thenReturn(List.of(
                    new CompetenceTransactionSubtypeSummary(compId, TransactionType.REVENUE, TransactionStatus.PENDING, TransactionSubtype.FIXED, 5L, new BigDecimal("1500.00")),
                    new CompetenceTransactionSubtypeSummary(compId, TransactionType.EXPENSE, TransactionStatus.PENDING, TransactionSubtype.FIXED, 7L, new BigDecimal("770.00")),
                    new CompetenceTransactionSubtypeSummary(compId, TransactionType.REVENUE, TransactionStatus.PENDING, TransactionSubtype.VARIABLE, 2L, new BigDecimal("250.00")),
                    new CompetenceTransactionSubtypeSummary(compId, TransactionType.EXPENSE, TransactionStatus.PENDING, TransactionSubtype.VARIABLE, 4L, new BigDecimal("580.00"))
            ));

            // When
            CompetenceDetailResponseV2 response = competenceUseCase.getByIdV2(compId, userId);

            // Then
            assertNotNull(response);
            assertEquals("06/2026", response.name());
            
            CompetenceDetailResponseV2.SummaryDetail pending = response.pendingSummary();
            assertEquals(18L, pending.transactionCount());
            
            assertEquals(new BigDecimal("1500.00"), pending.fixed().revenue());
            assertEquals(new BigDecimal("-770.00"), pending.fixed().expense());
            assertEquals(new BigDecimal("730.00"), pending.fixed().balance());
            
            assertEquals(new BigDecimal("250.00"), pending.variable().revenue());
            assertEquals(new BigDecimal("-580.00"), pending.variable().expense());
            assertEquals(new BigDecimal("-330.00"), pending.variable().balance());
            
            assertEquals(new BigDecimal("1750.00"), pending.totalSummary().totalRevenue());
            assertEquals(new BigDecimal("-1350.00"), pending.totalSummary().totalExpense());
            assertEquals(new BigDecimal("400.00"), pending.totalSummary().totalBalance());
        }

        @Test
        @DisplayName("Should throw CompetenceNotFoundException when not found")
        void givenNotExistingCompetence_whenGetByIdV2_thenThrows() {
            // Given
            UUID compId = UUID.randomUUID();
            when(competenceRepository.findById(compId)).thenReturn(Optional.empty());

            // When / Then
            assertThrows(CompetenceNotFoundException.class, () -> competenceUseCase.getByIdV2(compId, userId));
        }
    }

    @Nested
    @DisplayName("listAll")
    class ListAll {

        @Test
        @DisplayName("Should return competences ordered by year desc, month desc")
        void givenCompetences_whenListAll_thenReturnsOrdered() {
            // Given
            UUID febId = UUID.randomUUID();
            UUID janId = UUID.randomUUID();
            UUID decId = UUID.randomUUID();
            Competence feb2026 = createCompetence(febId, 2, 2026);
            Competence jan2026 = createCompetence(janId, 1, 2026);
            Competence dec2025 = createCompetence(decId, 12, 2025);

            when(competenceRepository.findByUserIdOrderByYearDescMonthDesc(userId))
                    .thenReturn(List.of(feb2026, jan2026, dec2025));
            when(transactionRepository.countTransactionsGroupedByCompetence(userId))
                    .thenReturn(List.of(
                            new CompetenceTransactionCountSummary(febId, 3L),
                            new CompetenceTransactionCountSummary(janId, 1L)
                    ));
            when(transactionRepository.sumAmountsGroupedByCompetence(userId))
                    .thenReturn(List.of(
                            new CompetenceTransactionAmountSummary(febId, TransactionType.REVENUE, TransactionStatus.COMPLETED, new BigDecimal("200.00")),
                            new CompetenceTransactionAmountSummary(febId, TransactionType.EXPENSE, TransactionStatus.COMPLETED, new BigDecimal("50.00")),
                            new CompetenceTransactionAmountSummary(janId, TransactionType.REVENUE, TransactionStatus.PENDING, new BigDecimal("150.00")),
                            new CompetenceTransactionAmountSummary(janId, TransactionType.EXPENSE, TransactionStatus.COMPLETED, new BigDecimal("30.00"))
                    ));

            // When
            List<CompetenceResponse> responses = competenceUseCase.listAll(userId);

            // Then
            assertEquals(3, responses.size());
            assertEquals("02/2026", responses.get(0).name());
            assertEquals(3L, responses.get(0).transactionCount());
            assertEquals(new BigDecimal("200.00"), responses.get(0).totalRevenue());
            assertEquals(new BigDecimal("50.00"), responses.get(0).totalExpense());

            assertEquals("01/2026", responses.get(1).name());
            assertEquals(1L, responses.get(1).transactionCount());
            assertEquals(new BigDecimal("150.00"), responses.get(1).totalRevenue());
            assertEquals(new BigDecimal("30.00"), responses.get(1).totalExpense());

            assertEquals("12/2025", responses.get(2).name());
            assertEquals(0L, responses.get(2).transactionCount());
            assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN), responses.get(2).totalRevenue());
            assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN), responses.get(2).totalExpense());
        }

        @Test
        @DisplayName("Should return empty list when no competences")
        void givenNoCompetences_whenListAll_thenReturnsEmpty() {
            // Given
            when(competenceRepository.findByUserIdOrderByYearDescMonthDesc(userId))
                    .thenReturn(List.of());
            when(transactionRepository.countTransactionsGroupedByCompetence(userId))
                    .thenReturn(List.of());
            when(transactionRepository.sumAmountsGroupedByCompetence(userId))
                    .thenReturn(List.of());

            // When
            List<CompetenceResponse> responses = competenceUseCase.listAll(userId);

            // Then
            assertTrue(responses.isEmpty());
        }
    }

    @Nested
    @DisplayName("getCurrent")
    class GetCurrent {

        @Test
        @DisplayName("Should return existing competence for current month/year")
        void givenExistingCurrent_whenGetCurrent_thenReturnsExisting() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            Integer currentMonth = now.getMonthValue();
            Integer currentYear = now.getYear();
            UUID existingId = UUID.randomUUID();
            Competence existing = createCompetence(existingId, currentMonth, currentYear);

            when(competenceRepository.findByUserIdAndMonthAndYear(userId, currentMonth, currentYear))
                    .thenReturn(Optional.of(existing));
            when(transactionRepository.countByCompetenceId(existingId))
                    .thenReturn(7L);
            when(transactionRepository.sumAmountsByCompetenceId(existingId))
                    .thenReturn(List.of());

            // When
            CompetenceDetailResponse response = competenceUseCase.getCurrent(userId);

            // Then
            assertEquals(existingId, response.id());
            assertEquals(currentMonth, response.month());
            assertEquals(currentYear, response.year());
            assertEquals(7L, response.transactionCount());
            assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN), response.totalRevenue());
            assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN), response.totalExpense());
            verify(competenceRepository, never()).save(any(Competence.class));
        }

        @Test
        @DisplayName("Should auto-create competence for current month/year when not exists")
        void givenNoCurrentCompetence_whenGetCurrent_thenAutoCreates() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            Integer currentMonth = now.getMonthValue();
            Integer currentYear = now.getYear();

            when(competenceRepository.findByUserIdAndMonthAndYear(userId, currentMonth, currentYear))
                    .thenReturn(Optional.empty());
            when(competenceRepository.save(any(Competence.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            CompetenceDetailResponse response = competenceUseCase.getCurrent(userId);

            // Then
            assertNotNull(response);
            assertEquals(currentMonth, response.month());
            assertEquals(currentYear, response.year());
            assertEquals(0L, response.transactionCount());
            verify(competenceRepository).save(any(Competence.class));
        }
    }

}
