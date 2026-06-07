package com.finance.app.infrastructure.persistence.repository;

import com.finance.app.infrastructure.persistence.entity.SubcategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubcategoryJpaRepository extends JpaRepository<SubcategoryEntity, UUID> {
    List<SubcategoryEntity> findByCategoryId(UUID categoryId);
    List<SubcategoryEntity> findByCategoryIdIn(List<UUID> categoryIds);
}
