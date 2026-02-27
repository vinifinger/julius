package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryJpaRepository extends JpaRepository<CategoryEntity, UUID> {

    List<CategoryEntity> findByUserId(UUID userId);

}
