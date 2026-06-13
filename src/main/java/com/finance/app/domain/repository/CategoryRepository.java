package com.finance.app.domain.repository;

import com.finance.app.domain.entity.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.finance.app.domain.entity.TransactionType;

public interface CategoryRepository {

    Optional<Category> findById(UUID id);

    List<Category> findByUserId(UUID userId);

    Optional<Category> findByUserIdAndNameAndType(UUID userId, String name, TransactionType type);

    Category save(Category category);

}
