package com.finance.app.domain.repository;

import com.finance.app.domain.entity.Subcategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubcategoryRepository {

    Subcategory save(Subcategory subcategory);

    List<Subcategory> saveAll(List<Subcategory> subcategories);

    Optional<Subcategory> findById(UUID id);

    List<Subcategory> findByCategoryId(UUID categoryId);

    List<Subcategory> findByCategoryIdIn(List<UUID> categoryIds);

    void delete(UUID id);

}
