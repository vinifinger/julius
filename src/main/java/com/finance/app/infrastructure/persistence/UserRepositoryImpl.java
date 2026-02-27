package com.finance.app.infrastructure.persistence;

import com.finance.app.domain.entity.User;
import com.finance.app.domain.repository.UserRepository;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import com.finance.app.infrastructure.persistence.mapper.UserMapper;
import com.finance.app.infrastructure.persistence.repository.UserJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserMapper mapper;

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public User save(User user) {
        UserEntity entity = mapper.toEntity(user);
        UserEntity savedEntity = jpaRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

}
