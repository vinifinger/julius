package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.infrastructure.config.JpaAuditingConfig;
import com.finance.app.infrastructure.persistence.entity.AccountEntity;
import com.finance.app.infrastructure.persistence.entity.CategoryEntity;
import com.finance.app.infrastructure.persistence.entity.CompetenceEntity;
import com.finance.app.infrastructure.persistence.entity.TransactionEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import com.finance.app.infrastructure.persistence.repository.AccountJpaRepository;
import com.finance.app.infrastructure.persistence.repository.CategoryJpaRepository;
import com.finance.app.infrastructure.persistence.repository.CompetenceJpaRepository;
import com.finance.app.infrastructure.persistence.repository.TransactionJpaRepository;
import com.finance.app.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class TransactionEntityRepositoryTest {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private AccountJpaRepository accountJpaRepository;

    @Autowired
    private CategoryJpaRepository categoryJpaRepository;

    @Autowired
    private CompetenceJpaRepository competenceJpaRepository;

    @Autowired
    private TransactionJpaRepository transactionJpaRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("Should save and retrieve a complete transaction with all relationships")
    void givenFullTransactionGraph_whenSaveAndRetrieve_thenAllRelationshipsAreValid() {
        // Given — create the full entity graph
        var user = userJpaRepository.save(UserEntity.builder()
                .name("Test User")
                .email("test@example.com")
                .passwordHash("hashed_password_123")
                .build());

        var account = accountJpaRepository.save(AccountEntity.builder()
                .user(user)
                .name("Main Account")
                .balance(BigDecimal.valueOf(1000.00))
                .currency("BRL")
                .build());

        var category = categoryJpaRepository.save(CategoryEntity.builder()
                .user(user)
                .name("Food")
                .colorHex("#FF5733")
                .build());

        var competence = competenceJpaRepository.save(CompetenceEntity.builder()
                .user(user)
                .month(2)
                .year(2026)
                .build());

        var transaction = transactionJpaRepository.save(TransactionEntity.builder()
                .account(account)
                .category(category)
                .competence(competence)
                .user(user)
                .description("Grocery shopping")
                .amount(BigDecimal.valueOf(150.50))
                .dateTime(LocalDateTime.of(2026, 2, 15, 10, 30))
                .status(TransactionStatus.PENDING)
                .build());

        // Flush and clear to force a real DB read
        entityManager.flush();
        entityManager.clear();

        // When — retrieve the transaction
        var found = transactionJpaRepository.findById(transaction.getId());

        // Then — validate entity and all relationships
        assertTrue(found.isPresent());
        var savedTransaction = found.get();

        assertNotNull(savedTransaction.getId());
        assertEquals("Grocery shopping", savedTransaction.getDescription());
        assertEquals(0, BigDecimal.valueOf(150.50).compareTo(savedTransaction.getAmount()));
        assertEquals(TransactionStatus.PENDING, savedTransaction.getStatus());

        // Validate FK relationships
        assertEquals(account.getId(), savedTransaction.getAccount().getId());
        assertEquals(category.getId(), savedTransaction.getCategory().getId());
        assertEquals(competence.getId(), savedTransaction.getCompetence().getId());
        assertEquals(user.getId(), savedTransaction.getUser().getId());
        assertNotNull(savedTransaction.getCreatedAt());
        assertNotNull(savedTransaction.getUpdatedAt());
    }

    @Test
    @DisplayName("Should save transaction with parent reference (self-join)")
    void givenParentTransaction_whenSaveChild_thenParentReferenceIsValid() {
        // Given
        var user = userJpaRepository.save(UserEntity.builder()
                .name("Test User")
                .email("parent-test@example.com")
                .passwordHash("hashed_password_123")
                .build());

        var account = accountJpaRepository.save(AccountEntity.builder()
                .user(user)
                .name("Account")
                .balance(BigDecimal.ZERO)
                .currency("BRL")
                .build());

        var category = categoryJpaRepository.save(CategoryEntity.builder()
                .user(user)
                .name("Bills")
                .build());

        var competence = competenceJpaRepository.save(CompetenceEntity.builder()
                .user(user)
                .month(3)
                .year(2026)
                .build());

        var parentTransaction = transactionJpaRepository.save(TransactionEntity.builder()
                .account(account)
                .category(category)
                .competence(competence)
                .user(user)
                .description("Monthly rent")
                .amount(BigDecimal.valueOf(2000.00))
                .dateTime(LocalDateTime.of(2026, 3, 1, 0, 0))
                .status(TransactionStatus.PAID)
                .build());

        // When — create child transaction referencing parent
        var childTransaction = transactionJpaRepository.save(TransactionEntity.builder()
                .account(account)
                .category(category)
                .competence(competence)
                .user(user)
                .parent(parentTransaction)
                .description("Rent installment 1")
                .amount(BigDecimal.valueOf(1000.00))
                .dateTime(LocalDateTime.of(2026, 3, 1, 0, 0))
                .status(TransactionStatus.PENDING)
                .build());

        entityManager.flush();
        entityManager.clear();

        // Then
        var found = transactionJpaRepository.findById(childTransaction.getId());
        assertTrue(found.isPresent());
        assertNotNull(found.get().getParent());
        assertEquals(parentTransaction.getId(), found.get().getParent().getId());
    }

}
