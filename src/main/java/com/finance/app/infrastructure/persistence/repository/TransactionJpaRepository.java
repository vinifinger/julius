package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.domain.entity.CategoryExpenseSummary;
import com.finance.app.domain.entity.CompetenceAmountSummary;
import com.finance.app.domain.entity.CompetenceTransactionCountSummary;
import com.finance.app.domain.entity.CompetenceTransactionAmountSummary;
import com.finance.app.domain.entity.CompetenceTransactionSubtypeSummary;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID>, JpaSpecificationExecutor<TransactionEntity> {

    List<TransactionEntity> findByUserId(UUID userId);

    List<TransactionEntity> findByAccountId(UUID accountId);

    List<TransactionEntity> findByCompetenceId(UUID competenceId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "WHERE t.competence.id = :competenceId AND t.type = :type AND t.status = 'COMPLETED'")
    BigDecimal sumAmountByCompetenceIdAndType(@Param("competenceId") UUID competenceId,
            @Param("type") TransactionType type);

    @Query("SELECT new com.finance.app.domain.entity.CategoryExpenseSummary(t.category.name, t.category.colorHex, COALESCE(SUM(t.amount), 0)) " +
            "FROM TransactionEntity t " +
            "WHERE t.competence.id = :competenceId AND t.type = 'EXPENSE' AND t.status = 'COMPLETED' " +
            "GROUP BY t.category.name, t.category.colorHex " +
            "ORDER BY SUM(t.amount) DESC")
    List<CategoryExpenseSummary> sumExpensesByCategory(@Param("competenceId") UUID competenceId);

    @Query("SELECT new com.finance.app.domain.entity.CompetenceAmountSummary(t.competence.id, t.type, COALESCE(SUM(t.amount), 0)) " +
            "FROM TransactionEntity t " +
            "WHERE t.competence.id IN :competenceIds AND t.status = 'COMPLETED' " +
            "GROUP BY t.competence.id, t.type")
    List<CompetenceAmountSummary> sumAmountByCompetenceIds(@Param("competenceIds") List<UUID> competenceIds);

    @Query("SELECT t FROM TransactionEntity t WHERE t.parent.id = :parentId OR t.id = :parentId")
    List<TransactionEntity> findByParentId(@Param("parentId") UUID parentId);

    @Query("SELECT COUNT(t) > 0 FROM TransactionEntity t WHERE t.account.id = :accountId AND t.status = 'PENDING'")
    boolean existsPendingByAccountId(@Param("accountId") UUID accountId);

    boolean existsByExternalId(String externalId);

    long countByCompetenceId(UUID competenceId);

    @Query("SELECT new com.finance.app.domain.entity.CompetenceTransactionCountSummary(t.competence.id, COUNT(t)) " +
            "FROM TransactionEntity t " +
            "WHERE t.user.id = :userId " +
            "GROUP BY t.competence.id")
    List<CompetenceTransactionCountSummary> countTransactionsGroupedByCompetence(@Param("userId") UUID userId);

    @Query("SELECT new com.finance.app.domain.entity.CompetenceTransactionAmountSummary(t.competence.id, t.type, t.status, COALESCE(SUM(t.amount), 0)) " +
            "FROM TransactionEntity t " +
            "WHERE t.user.id = :userId " +
            "GROUP BY t.competence.id, t.type, t.status")
    List<CompetenceTransactionAmountSummary> sumAmountsGroupedByCompetence(@Param("userId") UUID userId);

    @Query("SELECT new com.finance.app.domain.entity.CompetenceTransactionAmountSummary(t.competence.id, t.type, t.status, COALESCE(SUM(t.amount), 0)) " +
            "FROM TransactionEntity t " +
            "WHERE t.competence.id = :competenceId " +
            "GROUP BY t.competence.id, t.type, t.status")
    List<CompetenceTransactionAmountSummary> sumAmountsByCompetenceId(@Param("competenceId") UUID competenceId);

    @Query("SELECT new com.finance.app.domain.entity.CompetenceTransactionSubtypeSummary(t.competence.id, t.type, t.status, t.subtype, COUNT(t), COALESCE(SUM(t.amount), 0)) " +
            "FROM TransactionEntity t " +
            "WHERE t.competence.id = :competenceId " +
            "GROUP BY t.competence.id, t.type, t.status, t.subtype")
    List<CompetenceTransactionSubtypeSummary> sumSubtypeAmountsByCompetenceId(@Param("competenceId") UUID competenceId);

}
