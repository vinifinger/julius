package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

    List<AccountEntity> findByUserId(UUID userId);

    Optional<AccountEntity> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT COALESCE(SUM(a.balance), 0) FROM AccountEntity a WHERE a.user.id = :userId")
    BigDecimal sumBalanceByUserId(@Param("userId") UUID userId);

}
