package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import com.finance.app.domain.entity.TransactionType;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findByUserId(UUID userId);

    java.util.Optional<CategoryEntity> findByUserIdAndNameAndType(UUID userId, String name, TransactionType type);

}
