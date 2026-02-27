package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.web.dto.request.CreateAccountRequest;
import com.finance.app.web.dto.response.AccountResponse;
import com.finance.app.web.dto.response.BalanceResponse;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountUseCaseTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountUseCase accountUseCase;

    private final UUID userId = UUID.randomUUID();

    private Account createAccount(UUID id, String name, BigDecimal balance) {
        return Account.builder()
                .id(id)
                .userId(userId)
                .name(name)
                .balance(balance.setScale(2, RoundingMode.HALF_EVEN))
                .currency("BRL")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create account with default BRL currency when currency is null")
        void givenNullCurrency_whenCreate_thenDefaultsToBRL() {
            // Given
            CreateAccountRequest request = new CreateAccountRequest("Nubank", BigDecimal.valueOf(100.00), null);
            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            AccountResponse response = accountUseCase.create(request, userId);

            // Then
            assertNotNull(response);
            assertEquals("Nubank", response.name());
            assertEquals("BRL", response.currency());
            assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_EVEN), response.balance());
            verify(accountRepository).save(any(Account.class));
        }

        @Test
        @DisplayName("Should create account with provided currency")
        void givenCurrency_whenCreate_thenUsesThatCurrency() {
            // Given
            CreateAccountRequest request = new CreateAccountRequest("Dollar Account",
                    BigDecimal.valueOf(500.00), "USD");
            when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            AccountResponse response = accountUseCase.create(request, userId);

            // Then
            assertEquals("USD", response.currency());
        }
    }

    @Nested
    @DisplayName("listByUser")
    class ListByUser {

        @Test
        @DisplayName("Should return list of accounts for user")
        void givenUserId_whenListByUser_thenReturnsAccounts() {
            // Given
            Account account = createAccount(UUID.randomUUID(), "Carteira", BigDecimal.valueOf(50.00));
            when(accountRepository.findByUserId(userId)).thenReturn(List.of(account));

            // When
            List<AccountResponse> responses = accountUseCase.listByUser(userId);

            // Then
            assertEquals(1, responses.size());
            assertEquals("Carteira", responses.get(0).name());
        }

        @Test
        @DisplayName("Should return empty list when no accounts")
        void givenNoAccounts_whenListByUser_thenReturnsEmpty() {
            // Given
            when(accountRepository.findByUserId(userId)).thenReturn(List.of());

            // When
            List<AccountResponse> responses = accountUseCase.listByUser(userId);

            // Then
            assertTrue(responses.isEmpty());
        }
    }

    @Nested
    @DisplayName("getBalance")
    class GetBalance {

        @Test
        @DisplayName("Should return account with balance when found")
        void givenExistingId_whenGetBalance_thenReturnsAccount() {
            // Given
            UUID accountId = UUID.randomUUID();
            Account account = createAccount(accountId, "Nubank", BigDecimal.valueOf(250.00));
            when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.of(account));

            // When
            AccountResponse response = accountUseCase.getBalance(accountId, userId);

            // Then
            assertEquals(accountId, response.id());
            assertEquals(BigDecimal.valueOf(250.00).setScale(2, RoundingMode.HALF_EVEN), response.balance());
        }

        @Test
        @DisplayName("Should throw AccountNotFoundException when not found")
        void givenNonExistingId_whenGetBalance_thenThrows() {
            // Given
            UUID accountId = UUID.randomUUID();
            when(accountRepository.findByIdAndUserId(accountId, userId)).thenReturn(Optional.empty());

            // When / Then
            assertThrows(AccountNotFoundException.class, () -> accountUseCase.getBalance(accountId, userId));
        }
    }

    @Nested
    @DisplayName("getTotalBalance")
    class GetTotalBalance {

        @Test
        @DisplayName("Should return sum of all account balances for user (100 + 250 = 350)")
        void givenMultipleAccounts_whenGetTotalBalance_thenReturnsSumOf350() {
            // Given
            BigDecimal expectedTotal = BigDecimal.valueOf(350.00).setScale(2, RoundingMode.HALF_EVEN);
            when(accountRepository.sumBalanceByUserId(userId)).thenReturn(BigDecimal.valueOf(350.00));

            // When
            BalanceResponse response = accountUseCase.getTotalBalance(userId);

            // Then
            assertEquals(expectedTotal, response.totalBalance());
        }

        @Test
        @DisplayName("Should return zero when user has no accounts")
        void givenNoAccounts_whenGetTotalBalance_thenReturnsZero() {
            // Given
            when(accountRepository.sumBalanceByUserId(userId)).thenReturn(BigDecimal.ZERO);

            // When
            BalanceResponse response = accountUseCase.getTotalBalance(userId);

            // Then
            assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN), response.totalBalance());
        }
    }

}
