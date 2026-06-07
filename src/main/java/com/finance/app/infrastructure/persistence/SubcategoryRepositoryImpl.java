package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.Subcategory;
import com.finance.app.domain.repository.SubcategoryRepository;
import com.finance.app.infrastructure.persistence.entity.SubcategoryEntity;
import com.finance.app.infrastructure.persistence.mapper.SubcategoryMapper;
import com.finance.app.infrastructure.persistence.repository.SubcategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubcategoryRepositoryImpl implements SubcategoryRepository {

    private final SubcategoryJpaRepository jpaRepository;

    @Override
    public Subcategory save(Subcategory subcategory) {
        SubcategoryEntity entity = SubcategoryMapper.toEntity(subcategory);
        SubcategoryEntity saved = jpaRepository.save(entity);
        return SubcategoryMapper.toDomain(saved);
    }

    @Override
    public List<Subcategory> saveAll(List<Subcategory> subcategories) {
        List<SubcategoryEntity> entities = subcategories.stream()
                .map(SubcategoryMapper::toEntity)
                .toList();
        return jpaRepository.saveAll(entities).stream()
                .map(SubcategoryMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Subcategory> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(SubcategoryMapper::toDomain);
    }

    @Override
    public List<Subcategory> findByCategoryId(UUID categoryId) {
        return jpaRepository.findByCategoryId(categoryId).stream()
                .map(SubcategoryMapper::toDomain)
                .toList();
    }

    @Override
    public List<Subcategory> findByCategoryIdIn(List<UUID> categoryIds) {
        return jpaRepository.findByCategoryIdIn(categoryIds).stream()
                .map(SubcategoryMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID id) {
        jpaRepository.deleteById(id);
    }
}
