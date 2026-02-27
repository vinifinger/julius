package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.Category;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.infrastructure.persistence.entity.CategoryEntity;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import com.finance.app.infrastructure.persistence.mapper.CategoryMapper;
import com.finance.app.infrastructure.persistence.repository.CategoryJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryMapper mapper;
    private final EntityManager entityManager;

    @Override
    public Optional<Category> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Category> findByUserId(UUID userId) {
        return jpaRepository.findByUserId(userId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Category save(Category category) {
        UserEntity user = entityManager.getReference(UserEntity.class, category.getUserId());
        CategoryEntity entity = mapper.toEntity(category, user);
        CategoryEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

}
