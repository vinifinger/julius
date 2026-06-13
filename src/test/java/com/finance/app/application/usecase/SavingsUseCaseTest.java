package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Category;
import com.finance.app.domain.entity.Savings;
import com.finance.app.domain.entity.SavingsHistory;
import com.finance.app.domain.exception.SavingsNotFoundException;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.SavingsHistoryRepository;
import com.finance.app.domain.repository.SavingsRepository;
import com.finance.app.web.dto.request.CreateSavingsRequest;
import com.finance.app.web.dto.request.CreateTransactionRequest;
import com.finance.app.web.dto.request.SavingsTransactionRequest;
import com.finance.app.web.dto.response.TransactionResponse;
import com.finance.app.web.dto.response.SavingsResponse;
import com.finance.app.domain.entity.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsUseCaseTest {

    @Mock
    private SavingsRepository savingsRepository;

    @Mock
    private SavingsHistoryRepository savingsHistoryRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TransactionUseCase transactionUseCase;

    @InjectMocks
    private SavingsUseCase savingsUseCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID savingsId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();
    private final UUID competenceId = UUID.randomUUID();
    private final UUID categoryId = UUID.randomUUID();
    private final UUID transactionId = UUID.randomUUID();

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create savings vault")
        void givenValidRequest_whenCreate_thenReturnsSavingsResponse() {
            // Given
            CreateSavingsRequest request = new CreateSavingsRequest("Emergency Fund", new BigDecimal("1000.00"), "#FFFFFF", "icon-safe");
            when(savingsRepository.save(any(Savings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SavingsResponse response = savingsUseCase.create(request, userId);

            // Then
            assertNotNull(response);
            assertEquals("Emergency Fund", response.name());
            assertEquals(new BigDecimal("1000.00"), response.balance());
            verify(savingsRepository).save(any(Savings.class));
        }
    }

    @Nested
    @DisplayName("deposit")
    class Deposit {

        @Test
        @DisplayName("Should deposit money, create transaction and create history")
        void givenValidRequest_whenDeposit_thenUpdatesBalancesAndCreatesHistory() {
            // Given
            SavingsTransactionRequest request = new SavingsTransactionRequest(accountId, competenceId, new BigDecimal("200.00"), "Monthly deposit");
            
            Savings savings = Savings.builder().id(savingsId).userId(userId).balance(new BigDecimal("1000.00")).build();
            when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));
            
            Category category = Category.builder().id(categoryId).name("Savings Vault").userId(userId).type(TransactionType.EXPENSE).build();
            when(categoryRepository.findByUserIdAndNameAndType(userId, "Savings Vault", TransactionType.EXPENSE)).thenReturn(Optional.of(category));

            TransactionResponse txResponse = new TransactionResponse(
                    transactionId, accountId, categoryId, null, competenceId, userId, null, null, null, null,
                    "Monthly deposit", new BigDecimal("200.00"), LocalDateTime.now(), "EXPENSE", null, "COMPLETED",
                    LocalDateTime.now(), LocalDateTime.now());
            when(transactionUseCase.create(any(CreateTransactionRequest.class), eq(userId))).thenReturn(txResponse);
            
            when(savingsRepository.save(any(Savings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SavingsResponse response = savingsUseCase.deposit(savingsId, request, userId);

            // Then
            assertNotNull(response);
            assertEquals(new BigDecimal("1200.00"), savings.getBalance()); // Savings increased
            
            verify(transactionUseCase).create(any(CreateTransactionRequest.class), eq(userId));
            verify(savingsRepository).save(savings);
            verify(savingsHistoryRepository).save(any(SavingsHistory.class));
        }

        @Test
        @DisplayName("Should throw SavingsNotFoundException when savings does not exist")
        void givenInvalidSavings_whenDeposit_thenThrowsException() {
            SavingsTransactionRequest request = new SavingsTransactionRequest(accountId, competenceId, new BigDecimal("200.00"), "Monthly deposit");
            when(savingsRepository.findById(savingsId)).thenReturn(Optional.empty());

            assertThrows(SavingsNotFoundException.class, () -> savingsUseCase.deposit(savingsId, request, userId));
        }
    }

    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        @DisplayName("Should withdraw money, create transaction and create history")
        void givenValidRequest_whenWithdraw_thenUpdatesBalancesAndCreatesHistory() {
            // Given
            SavingsTransactionRequest request = new SavingsTransactionRequest(accountId, competenceId, new BigDecimal("200.00"), "Need cash");
            
            Savings savings = Savings.builder().id(savingsId).userId(userId).balance(new BigDecimal("1000.00")).build();
            when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));
            
            Category category = Category.builder().id(categoryId).name("Savings Vault").userId(userId).type(TransactionType.REVENUE).build();
            when(categoryRepository.findByUserIdAndNameAndType(userId, "Savings Vault", TransactionType.REVENUE)).thenReturn(Optional.of(category));

            TransactionResponse txResponse = new TransactionResponse(
                    transactionId, accountId, categoryId, null, competenceId, userId, null, null, null, null,
                    "Need cash", new BigDecimal("200.00"), LocalDateTime.now(), "REVENUE", null, "COMPLETED",
                    LocalDateTime.now(), LocalDateTime.now());
            when(transactionUseCase.create(any(CreateTransactionRequest.class), eq(userId))).thenReturn(txResponse);
            
            when(savingsRepository.save(any(Savings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SavingsResponse response = savingsUseCase.withdraw(savingsId, request, userId);

            // Then
            assertNotNull(response);
            assertEquals(new BigDecimal("800.00"), savings.getBalance()); // Savings decreased
            
            verify(transactionUseCase).create(any(CreateTransactionRequest.class), eq(userId));
            verify(savingsRepository).save(savings);
            verify(savingsHistoryRepository).save(any(SavingsHistory.class));
        }

        @Test
        @DisplayName("Should throw exception when insufficient funds in savings")
        void givenInsufficientFunds_whenWithdraw_thenThrowsException() {
            SavingsTransactionRequest request = new SavingsTransactionRequest(accountId, competenceId, new BigDecimal("2000.00"), "Need cash");
            
            Savings savings = Savings.builder().id(savingsId).userId(userId).balance(new BigDecimal("1000.00")).build();
            when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));

            assertThrows(IllegalArgumentException.class, () -> savingsUseCase.withdraw(savingsId, request, userId));
        }
    }
}
