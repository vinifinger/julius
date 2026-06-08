package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Savings;
import com.finance.app.domain.entity.SavingsHistory;
import com.finance.app.domain.entity.SavingsHistoryType;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.SavingsNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.SavingsHistoryRepository;
import com.finance.app.domain.repository.SavingsRepository;
import com.finance.app.web.dto.request.CreateSavingsRequest;
import com.finance.app.web.dto.request.SavingsTransactionRequest;
import com.finance.app.web.dto.response.SavingsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingsUseCaseTest {

    @Mock
    private SavingsRepository savingsRepository;

    @Mock
    private SavingsHistoryRepository savingsHistoryRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private SavingsUseCase savingsUseCase;

    private final UUID userId = UUID.randomUUID();
    private final UUID savingsId = UUID.randomUUID();
    private final UUID accountId = UUID.randomUUID();

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
        @DisplayName("Should deposit money, deduct from account and create history")
        void givenValidRequest_whenDeposit_thenUpdatesBalancesAndCreatesHistory() {
            // Given
            SavingsTransactionRequest request = new SavingsTransactionRequest(accountId, new BigDecimal("200.00"), "Monthly deposit");
            
            Savings savings = Savings.builder().id(savingsId).userId(userId).balance(new BigDecimal("1000.00")).build();
            when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));
            
            Account account = Account.builder().id(accountId).userId(userId).balance(new BigDecimal("500.00")).build();
            when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(account));
            
            when(savingsRepository.save(any(Savings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SavingsResponse response = savingsUseCase.deposit(savingsId, request, userId);

            // Then
            assertNotNull(response);
            assertEquals(new BigDecimal("1200.00"), savings.getBalance()); // Savings increased
            assertEquals(new BigDecimal("300.00"), account.getBalance());  // Account decreased
            
            verify(accountRepository).save(account);
            verify(savingsRepository).save(savings);
            verify(savingsHistoryRepository).save(any(SavingsHistory.class));
        }

        @Test
        @DisplayName("Should throw SavingsNotFoundException when savings does not exist")
        void givenInvalidSavings_whenDeposit_thenThrowsException() {
            SavingsTransactionRequest request = new SavingsTransactionRequest(accountId, new BigDecimal("200.00"), "Monthly deposit");
            when(savingsRepository.findById(savingsId)).thenReturn(Optional.empty());

            assertThrows(SavingsNotFoundException.class, () -> savingsUseCase.deposit(savingsId, request, userId));
        }
    }

    @Nested
    @DisplayName("withdraw")
    class Withdraw {

        @Test
        @DisplayName("Should withdraw money, add to account and create history")
        void givenValidRequest_whenWithdraw_thenUpdatesBalancesAndCreatesHistory() {
            // Given
            SavingsTransactionRequest request = new SavingsTransactionRequest(accountId, new BigDecimal("200.00"), "Need cash");
            
            Savings savings = Savings.builder().id(savingsId).userId(userId).balance(new BigDecimal("1000.00")).build();
            when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));
            
            Account account = Account.builder().id(accountId).userId(userId).balance(new BigDecimal("500.00")).build();
            when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(account));
            
            when(savingsRepository.save(any(Savings.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            SavingsResponse response = savingsUseCase.withdraw(savingsId, request, userId);

            // Then
            assertNotNull(response);
            assertEquals(new BigDecimal("800.00"), savings.getBalance()); // Savings decreased
            assertEquals(new BigDecimal("700.00"), account.getBalance()); // Account increased
            
            verify(accountRepository).save(account);
            verify(savingsRepository).save(savings);
            verify(savingsHistoryRepository).save(any(SavingsHistory.class));
        }

        @Test
        @DisplayName("Should throw exception when insufficient funds in savings")
        void givenInsufficientFunds_whenWithdraw_thenThrowsException() {
            SavingsTransactionRequest request = new SavingsTransactionRequest(accountId, new BigDecimal("2000.00"), "Need cash");
            
            Savings savings = Savings.builder().id(savingsId).userId(userId).balance(new BigDecimal("1000.00")).build();
            when(savingsRepository.findById(savingsId)).thenReturn(Optional.of(savings));

            assertThrows(IllegalArgumentException.class, () -> savingsUseCase.withdraw(savingsId, request, userId));
        }
    }
}
