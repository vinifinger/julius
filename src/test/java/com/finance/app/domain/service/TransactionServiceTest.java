package com.finance.app.domain.service;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionServiceTest {

    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        transactionService = new TransactionService();
    }

    private Account createAccountWithBalance(BigDecimal balance) {
        return Account.builder()
                .id(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .name("Test Account")
                .balance(balance.setScale(2, RoundingMode.HALF_EVEN))
                .currency("BRL")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private Transaction createTransaction(BigDecimal amount, TransactionType type, TransactionStatus status) {
        return Transaction.builder()
                .id(UUID.randomUUID())
                .accountId(UUID.randomUUID())
                .categoryId(UUID.randomUUID())
                .competenceId(UUID.randomUUID())
                .userId(UUID.randomUUID())
                .description("Test Transaction")
                .amount(amount.setScale(2, RoundingMode.HALF_EVEN))
                .dateTime(LocalDateTime.now())
                .type(type)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("processTransaction")
    class ProcessTransaction {

        @Test
        @DisplayName("Given balance 100.00 and EXPENSE of 30.55 PAID, then balance should be 69.45")
        void givenExpensePaid_whenProcess_thenSubtractsFromBalance() {
            // Given
            Account account = createAccountWithBalance(BigDecimal.valueOf(100.00));
            Transaction transaction = createTransaction(
                    BigDecimal.valueOf(30.55), TransactionType.EXPENSE, TransactionStatus.PAID);

            // When
            transactionService.processTransaction(transaction, account);

            // Then
            assertEquals(BigDecimal.valueOf(69.45).setScale(2, RoundingMode.HALF_EVEN), account.getBalance());
        }

        @Test
        @DisplayName("Given balance 100.00 and REVENUE of 50.00 PAID, then balance should be 150.00")
        void givenRevenuePaid_whenProcess_thenAddsToBalance() {
            // Given
            Account account = createAccountWithBalance(BigDecimal.valueOf(100.00));
            Transaction transaction = createTransaction(
                    BigDecimal.valueOf(50.00), TransactionType.REVENUE, TransactionStatus.PAID);

            // When
            transactionService.processTransaction(transaction, account);

            // Then
            assertEquals(BigDecimal.valueOf(150.00).setScale(2, RoundingMode.HALF_EVEN), account.getBalance());
        }

        @Test
        @DisplayName("Given PENDING transaction, then balance should not change")
        void givenPendingTransaction_whenProcess_thenBalanceUnchanged() {
            // Given
            Account account = createAccountWithBalance(BigDecimal.valueOf(100.00));
            Transaction transaction = createTransaction(
                    BigDecimal.valueOf(30.55), TransactionType.EXPENSE, TransactionStatus.PENDING);

            // When
            transactionService.processTransaction(transaction, account);

            // Then
            assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_EVEN), account.getBalance());
        }
    }

    @Nested
    @DisplayName("reverseTransaction")
    class ReverseTransaction {

        @Test
        @DisplayName("Given EXPENSE reversal, then balance should increase")
        void givenPaidExpense_whenReverse_thenAddsToBalance() {
            // Given
            Account account = createAccountWithBalance(BigDecimal.valueOf(69.45));
            Transaction transaction = createTransaction(
                    BigDecimal.valueOf(30.55), TransactionType.EXPENSE, TransactionStatus.PAID);

            // When
            transactionService.reverseTransaction(transaction, account);

            // Then
            assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_EVEN), account.getBalance());
        }

        @Test
        @DisplayName("Given REVENUE reversal, then balance should decrease")
        void givenPaidRevenue_whenReverse_thenSubtractsFromBalance() {
            // Given
            Account account = createAccountWithBalance(BigDecimal.valueOf(150.00));
            Transaction transaction = createTransaction(
                    BigDecimal.valueOf(50.00), TransactionType.REVENUE, TransactionStatus.PAID);

            // When
            transactionService.reverseTransaction(transaction, account);

            // Then
            assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_EVEN), account.getBalance());
        }
    }

}
