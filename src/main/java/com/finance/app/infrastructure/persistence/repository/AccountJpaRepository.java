package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountJpaRepository extends JpaRepository<AccountEntity, UUID> {

    List<AccountEntity> findByUserId(UUID userId);

}
