package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.domain.entity.CategoryExpenseSummary;
import com.finance.app.domain.entity.CompetenceAmountSummary;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, UUID> {

    List<TransactionEntity> findByUserId(UUID userId);

    List<TransactionEntity> findByAccountId(UUID accountId);

    List<TransactionEntity> findByCompetenceId(UUID competenceId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionEntity t " +
            "WHERE t.competence.id = :competenceId AND t.type = :type AND t.status = 'PAID'")
    BigDecimal sumAmountByCompetenceIdAndType(@Param("competenceId") UUID competenceId,
            @Param("type") TransactionType type);

    @Query("SELECT t.category.name, t.category.colorHex, COALESCE(SUM(t.amount), 0) " +
            "FROM TransactionEntity t " +
            "WHERE t.competence.id = :competenceId AND t.type = 'EXPENSE' AND t.status = 'PAID' " +
            "GROUP BY t.category.name, t.category.colorHex " +
            "ORDER BY SUM(t.amount) DESC")
    List<CategoryExpenseSummary> sumExpensesByCategory(@Param("competenceId") UUID competenceId);

    @Query("SELECT t.competence.id, t.type, COALESCE(SUM(t.amount), 0) " +
            "FROM TransactionEntity t " +
            "WHERE t.competence.id IN :competenceIds AND t.status = 'PAID' " +
            "GROUP BY t.competence.id, t.type")
    List<CompetenceAmountSummary> sumAmountByCompetenceIds(@Param("competenceIds") List<UUID> competenceIds);

}
