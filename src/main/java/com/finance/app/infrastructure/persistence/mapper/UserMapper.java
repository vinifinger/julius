package com.finance.app.infrastructure.persistence.mapper;

import com.finance.app.domain.entity.User;
import com.finance.app.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toDomain(UserEntity entity) {
        return User.builder()
                .id(entity.getId())
                .name(entity.getName())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public UserEntity toEntity(User user) {
        return UserEntity.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

}
