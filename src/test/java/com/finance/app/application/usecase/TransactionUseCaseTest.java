package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.InvalidTransactionException;
import com.finance.app.domain.exception.TransactionNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.domain.service.TransactionService;
import com.finance.app.web.dto.request.CreateTransactionRequest;
import com.finance.app.web.dto.request.UpdateTransactionStatusRequest;
import com.finance.app.web.dto.response.TransactionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionUseCaseTest {

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
    private TransactionUseCase transactionUseCase;

    private final UUID accountId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID competenceId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    private Account createAccount(BigDecimal balance) {
        return Account.builder()
                .id(accountId)
                .userId(userId)
                .name("Main Account")
                .balance(balance.setScale(2, RoundingMode.HALF_EVEN))
                .currency("BRL")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Transaction createTransaction(UUID id, TransactionType type, TransactionStatus status) {
        return Transaction.builder()
                .id(id)
                .accountId(accountId)
                .categoryId(categoryId)
                .competenceId(competenceId)
                .userId(userId)
                .description("Test")
                .amount(BigDecimal.valueOf(50.00).setScale(2, RoundingMode.HALF_EVEN))
                .dateTime(LocalDateTime.now())
                .type(type)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create transaction with PAID status and process balance")
        void givenPaidTransaction_whenCreate_thenProcessesBalance() {
            // Given
            Account account = createAccount(BigDecimal.valueOf(1000.00));
            CreateTransactionRequest request = new CreateTransactionRequest(
                    accountId, categoryId, competenceId,
                    "Grocery shopping", BigDecimal.valueOf(50.00),
                    LocalDateTime.now(), "EXPENSE", "PAID");

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(
                    com.finance.app.domain.entity.Category.builder().id(categoryId).build()));
            when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(
                    com.finance.app.domain.entity.Competence.builder().id(competenceId).build()));
            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            TransactionResponse response = transactionUseCase.create(request, userId);

            // Then
            assertNotNull(response);
            assertEquals("EXPENSE", response.type());
            assertEquals("PAID", response.status());
            verify(transactionService).processTransaction(any(Transaction.class), any(Account.class));
            verify(accountRepository).save(account);
        }

        @Test
        @DisplayName("Should create transaction with PENDING status without processing balance")
        void givenPendingTransaction_whenCreate_thenDoesNotProcessBalance() {
            // Given
            Account account = createAccount(BigDecimal.valueOf(1000.00));
            CreateTransactionRequest request = new CreateTransactionRequest(
                    accountId, categoryId, competenceId,
                    "Future expense", BigDecimal.valueOf(100.00),
                    LocalDateTime.now(), "EXPENSE", "PENDING");

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(
                    com.finance.app.domain.entity.Category.builder().id(categoryId).build()));
            when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(
                    com.finance.app.domain.entity.Competence.builder().id(competenceId).build()));
            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            // When
            TransactionResponse response = transactionUseCase.create(request, userId);

            // Then
            assertNotNull(response);
            assertEquals("PENDING", response.status());
            verify(transactionService).processTransaction(any(Transaction.class), any(Account.class));
            verify(accountRepository).save(account);
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when account does not exist")
        void givenInvalidAccount_whenCreate_thenThrowsAccountNotFound() {
            // Given
            CreateTransactionRequest request = new CreateTransactionRequest(
                    accountId, categoryId, competenceId,
                    "Test", BigDecimal.valueOf(50.00),
                    LocalDateTime.now(), "EXPENSE", "PAID");

            when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

            // When / Then
            assertThrows(AccountNotFoundException.class, () -> transactionUseCase.create(request, userId));
            verify(transactionRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw InvalidTransactionException for invalid type")
        void givenInvalidType_whenCreate_thenThrowsInvalidTransaction() {
            // Given
            Account account = createAccount(BigDecimal.valueOf(1000.00));
            CreateTransactionRequest request = new CreateTransactionRequest(
                    accountId, categoryId, competenceId,
                    "Test", BigDecimal.valueOf(50.00),
                    LocalDateTime.now(), "INVALID", "PAID");

            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(
                    com.finance.app.domain.entity.Category.builder().id(categoryId).build()));
            when(competenceRepository.findById(competenceId)).thenReturn(Optional.of(
                    com.finance.app.domain.entity.Competence.builder().id(competenceId).build()));

            // When / Then
            assertThrows(InvalidTransactionException.class, () -> transactionUseCase.create(request, userId));
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Should return transaction when found")
        void givenExistingId_whenGetById_thenReturnsResponse() {
            // Given
            UUID transactionId = UUID.randomUUID();
            Transaction transaction = createTransaction(transactionId, TransactionType.EXPENSE, TransactionStatus.PAID);
            when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

            // When
            TransactionResponse response = transactionUseCase.getById(transactionId);

            // Then
            assertNotNull(response);
            assertEquals(transactionId, response.id());
        }

        @Test
        @DisplayName("Should throw TransactionNotFoundException when not found")
        void givenNonExistingId_whenGetById_thenThrows() {
            // Given
            UUID transactionId = UUID.randomUUID();
            when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

            // When / Then
            assertThrows(TransactionNotFoundException.class, () -> transactionUseCase.getById(transactionId));
        }
    }

    @Nested
    @DisplayName("listByUser")
    class ListByUser {

        @Test
        @DisplayName("Should return list of transactions for user")
        void givenUserId_whenListByUser_thenReturnsTransactions() {
            // Given
            Transaction transaction = createTransaction(UUID.randomUUID(), TransactionType.EXPENSE,
                    TransactionStatus.PAID);
            when(transactionRepository.findByUserId(userId)).thenReturn(List.of(transaction));

            // When
            List<TransactionResponse> responses = transactionUseCase.listByUser(userId);

            // Then
            assertEquals(1, responses.size());
        }

        @Test
        @DisplayName("Should return empty list when no transactions")
        void givenNoTransactions_whenListByUser_thenReturnsEmpty() {
            // Given
            when(transactionRepository.findByUserId(userId)).thenReturn(List.of());

            // When
            List<TransactionResponse> responses = transactionUseCase.listByUser(userId);

            // Then
            assertTrue(responses.isEmpty());
        }
    }

    @Nested
    @DisplayName("updateStatus")
    class UpdateStatus {

        @Test
        @DisplayName("Should process balance when changing PENDING to PAID")
        void givenPendingToPaid_whenUpdateStatus_thenProcessesBalance() {
            // Given
            UUID transactionId = UUID.randomUUID();
            Transaction transaction = createTransaction(transactionId, TransactionType.EXPENSE,
                    TransactionStatus.PENDING);
            Account account = createAccount(BigDecimal.valueOf(1000.00));

            when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest("PAID");

            // When
            TransactionResponse response = transactionUseCase.updateStatus(transactionId, request);

            // Then
            assertEquals("PAID", response.status());
            verify(transactionService).processTransaction(any(Transaction.class), any(Account.class));
            verify(accountRepository).save(account);
        }

        @Test
        @DisplayName("Should reverse balance when changing PAID to PENDING")
        void givenPaidToPending_whenUpdateStatus_thenReversesBalance() {
            // Given
            UUID transactionId = UUID.randomUUID();
            Transaction transaction = createTransaction(transactionId, TransactionType.EXPENSE, TransactionStatus.PAID);
            Account account = createAccount(BigDecimal.valueOf(950.00));

            when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
            when(transactionRepository.save(any(Transaction.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            UpdateTransactionStatusRequest request = new UpdateTransactionStatusRequest("PENDING");

            // When
            TransactionResponse response = transactionUseCase.updateStatus(transactionId, request);

            // Then
            assertEquals("PENDING", response.status());
            verify(transactionService).reverseTransaction(any(Transaction.class), any(Account.class));
            verify(accountRepository).save(account);
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Should reverse balance and delete when transaction is PAID")
        void givenPaidTransaction_whenDelete_thenReversesBalanceAndDeletes() {
            // Given
            UUID transactionId = UUID.randomUUID();
            Transaction transaction = createTransaction(transactionId, TransactionType.EXPENSE, TransactionStatus.PAID);
            Account account = createAccount(BigDecimal.valueOf(950.00));

            when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));
            when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

            // When
            transactionUseCase.delete(transactionId);

            // Then
            verify(transactionService).reverseTransaction(transaction, account);
            verify(accountRepository).save(account);
            verify(transactionRepository).delete(transactionId);
        }

        @Test
        @DisplayName("Should delete without reversing when transaction is PENDING")
        void givenPendingTransaction_whenDelete_thenDeletesWithoutReversing() {
            // Given
            UUID transactionId = UUID.randomUUID();
            Transaction transaction = createTransaction(transactionId, TransactionType.EXPENSE,
                    TransactionStatus.PENDING);

            when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

            // When
            transactionUseCase.delete(transactionId);

            // Then
            verify(transactionService, never()).reverseTransaction(any(), any());
            verify(accountRepository, never()).save(any());
            verify(transactionRepository).delete(transactionId);
        }
    }

}
