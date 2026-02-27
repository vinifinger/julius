package com.finance.app.domain.repository;

import com.finance.app.domain.entity.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Optional<Category> findById(UUID id);

    List<Category> findByUserId(UUID userId);

    Category save(Category category);

}
