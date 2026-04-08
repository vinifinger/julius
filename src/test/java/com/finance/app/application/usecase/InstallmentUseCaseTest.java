package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Category;
import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.InstallmentSeries;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.InstallmentValidationException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.domain.service.TransactionService;
import com.finance.app.web.dto.request.CreateInstallmentRequest;
import com.finance.app.web.dto.request.UpdateInstallmentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class InstallmentUseCaseTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CompetenceRepository competenceRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private InstallmentUseCase installmentUseCase;

    @Captor
    private ArgumentCaptor<List<Transaction>> transactionsCaptor;

    private final UUID userId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID competenceId = UUID.randomUUID();

    private Account mockAccount() {
        return Account.builder().id(accountId).userId(userId).build();
    }

    private Category mockCategory() {
        return Category.builder().id(categoryId).build();
    }

    private Competence mockCompetence(int month, int year) {
        return Competence.builder().id(UUID.randomUUID()).userId(userId).month(month).year(year).build();
    }

    @Nested
    @DisplayName("Create Installment Series")
    class CreateSeries {

        @Test
        @DisplayName("Should create 3 exact installments of 100")
        void shouldCreateExactInstallments() {
             when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(mockAccount()));
             when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory()));
             when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(mockCompetence(1, 2026)));
             when(competenceRepository.findByUserIdAndMonthAndYear(eq(userId), anyInt(), anyInt()))
                    .thenAnswer(i -> Optional.of(mockCompetence(i.getArgument(1), i.getArgument(2))));
             
             when(transactionRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

             CreateInstallmentRequest request = new CreateInstallmentRequest(
                     accountId, categoryId, competenceId, "Purchase",
                     new BigDecimal("300.00"), null, 3, LocalDateTime.now(), TransactionType.EXPENSE, TransactionStatus.PENDING
             );

             InstallmentSeries series = installmentUseCase.createInstallmentSeries(request, userId);

             verify(transactionRepository).saveAll(transactionsCaptor.capture());
             List<Transaction> saved = transactionsCaptor.getValue();
             
             assertEquals(3, saved.size());
             assertEquals(new BigDecimal("100.00"), saved.get(0).getAmount());
             assertEquals(new BigDecimal("100.00"), saved.get(1).getAmount());
             assertEquals(new BigDecimal("100.00"), saved.get(2).getAmount());
             assertEquals(new BigDecimal("300.00"), series.totalAmount());
             assertEquals(3, series.totalInstallments());
             assertEquals(3, series.pendingInstallments());
        }

        @Test
        @DisplayName("Should apply residue to the last installment")
        void shouldApplyResidueToLastInstallment() {
             when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(mockAccount()));
             when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory()));
             when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(mockCompetence(1, 2026)));
             when(competenceRepository.findByUserIdAndMonthAndYear(eq(userId), anyInt(), anyInt()))
                    .thenAnswer(i -> Optional.of(mockCompetence(i.getArgument(1), i.getArgument(2))));
             
             when(transactionRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

             CreateInstallmentRequest request = new CreateInstallmentRequest(
                     accountId, categoryId, competenceId, "Purchase",
                     new BigDecimal("100.01"), null, 3, LocalDateTime.now(), TransactionType.EXPENSE, TransactionStatus.PENDING
             );

             InstallmentSeries series = installmentUseCase.createInstallmentSeries(request, userId);

             verify(transactionRepository).saveAll(transactionsCaptor.capture());
             List<Transaction> saved = transactionsCaptor.getValue();
             
             assertEquals(new BigDecimal("33.34"), saved.get(0).getAmount());
             assertEquals(new BigDecimal("33.34"), saved.get(1).getAmount());
             assertEquals(new BigDecimal("33.33"), saved.get(2).getAmount());
             assertEquals(new BigDecimal("100.01"), series.totalAmount());
        }

        @Test
        @DisplayName("Should respect year rollover (e.g. November to February)")
        void shouldRespectYearRollover() {
             when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(mockAccount()));
             when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(mockCategory()));
             when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(mockCompetence(11, 2026))); // Nov
             
             CreateInstallmentRequest request = new CreateInstallmentRequest(
                     accountId, categoryId, competenceId, "Purchase",
                     new BigDecimal("400.00"), null, 4, LocalDateTime.now(), TransactionType.EXPENSE, TransactionStatus.PENDING
             );

             // Only mock findByUserIdAndMonthAndYear matching the Rollover checks.
             when(competenceRepository.findByUserIdAndMonthAndYear(userId, 11, 2026)).thenReturn(Optional.of(mockCompetence(11, 2026)));
             when(competenceRepository.findByUserIdAndMonthAndYear(userId, 12, 2026)).thenReturn(Optional.of(mockCompetence(12, 2026)));
             when(competenceRepository.findByUserIdAndMonthAndYear(userId, 1, 2027)).thenReturn(Optional.of(mockCompetence(1, 2027)));
             when(competenceRepository.findByUserIdAndMonthAndYear(userId, 2, 2027)).thenReturn(Optional.of(mockCompetence(2, 2027)));

             when(transactionRepository.saveAll(any())).thenAnswer(i -> i.getArgument(0));

             installmentUseCase.createInstallmentSeries(request, userId);

             verify(transactionRepository).saveAll(transactionsCaptor.capture());
             List<Transaction> saved = transactionsCaptor.getValue();
             
             assertEquals(4, saved.size());
             // Competence interactions validated via strict mocks/returns above.
        }

        @Test
        @DisplayName("Should reject installments < 2")
        void shouldRejectSingleInstallment() {
             CreateInstallmentRequest request = new CreateInstallmentRequest(
                     accountId, categoryId, competenceId, "Purchase",
                     new BigDecimal("100.00"), null, 1, LocalDateTime.now(), TransactionType.EXPENSE, TransactionStatus.PENDING
             );

             assertThrows(InstallmentValidationException.class, () -> 
                  installmentUseCase.createInstallmentSeries(request, userId)
             );
        }

        @Test
        @DisplayName("Should reject arithmetic divergence in hybrid input")
        void shouldRejectDivergence() {
             // 3 installments of 33.00 != 100.00 (Delta = 1.00, allowed delta = 0.03)
             CreateInstallmentRequest request = new CreateInstallmentRequest(
                     accountId, categoryId, competenceId, "Purchase",
                     new BigDecimal("100.00"), new BigDecimal("33.00"), 3, LocalDateTime.now(), TransactionType.EXPENSE, TransactionStatus.PENDING
             );

             assertThrows(InstallmentValidationException.class, () -> 
                  installmentUseCase.createInstallmentSeries(request, userId)
             );
        }
    }

    @Nested
    @DisplayName("Protected Edit (Update Series)")
    class UpdateSeries {
        
        @Test
        @DisplayName("Should recalculate only pending installments")
        void shouldRecalculatePendingOnly() {
            UUID parentId = UUID.randomUUID();
            Transaction paid1 = Transaction.builder().id(parentId).parentId(parentId).installmentNumber(1).amount(new BigDecimal("100.00")).status(TransactionStatus.PAID).build();
            Transaction paid2 = Transaction.builder().id(UUID.randomUUID()).parentId(parentId).installmentNumber(2).amount(new BigDecimal("100.00")).status(TransactionStatus.PAID).build();
            Transaction pending3 = Transaction.builder().id(UUID.randomUUID()).parentId(parentId).installmentNumber(3).amount(new BigDecimal("100.00")).status(TransactionStatus.PENDING).build();
            Transaction pending4 = Transaction.builder().id(UUID.randomUUID()).parentId(parentId).installmentNumber(4).amount(new BigDecimal("100.00")).status(TransactionStatus.PENDING).build();
            
            when(transactionRepository.findByParentId(parentId)).thenReturn(List.of(paid1, paid2, pending3, pending4));
            
            // New total = 600. Paid so far = 200. Remaining = 400. 
            // 2 pending -> each should be 200.
            UpdateInstallmentRequest request = new UpdateInstallmentRequest(new BigDecimal("600.00"));
            
            installmentUseCase.updateInstallmentSeries(parentId, request);
            
            assertEquals(new BigDecimal("100.00"), paid1.getAmount());
            assertEquals(new BigDecimal("100.00"), paid2.getAmount());
            assertEquals(new BigDecimal("200.00"), pending3.getAmount());
            assertEquals(new BigDecimal("200.00"), pending4.getAmount());
            
            verify(transactionRepository, times(2)).save(any(Transaction.class));
        }
    }
    
    @Nested
    @DisplayName("Change Installment Type")
    class ChangeType {

        @Test
        @DisplayName("Should invert balance for paid installments when changing type")
        void shouldInvertBalanceForPaid() {
            UUID parentId = UUID.randomUUID();
            Account account = mockAccount();
            Transaction paid = Transaction.builder().id(parentId).parentId(parentId).accountId(accountId).userId(userId)
                    .amount(BigDecimal.valueOf(100.00)).type(TransactionType.EXPENSE).status(TransactionStatus.PAID).build();
            Transaction pending = Transaction.builder().id(UUID.randomUUID()).parentId(parentId).accountId(accountId).userId(userId)
                    .amount(BigDecimal.valueOf(100.00)).type(TransactionType.EXPENSE).status(TransactionStatus.PENDING).build();

            when(transactionRepository.findByParentId(parentId)).thenReturn(List.of(paid, pending));
            when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(account));
            
            installmentUseCase.changeInstallmentType(parentId, TransactionType.REVENUE);

            assertEquals(TransactionType.REVENUE, paid.getType());
            assertEquals(TransactionType.REVENUE, pending.getType());

            verify(transactionService).reverseTransaction(paid, account);
            verify(transactionService).processTransaction(paid, account);
            verify(transactionRepository).saveAll(any());
            verify(accountRepository).save(account);
        }
    }
}
