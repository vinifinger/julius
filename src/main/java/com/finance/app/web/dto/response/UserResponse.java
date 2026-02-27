package com.finance.app.web.dto.response;

import com.finance.app.domain.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String email,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static UserResponse fromDomain(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt());
    }

}
