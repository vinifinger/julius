package com.finance.app.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private UUID id;
    private String name;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static User create(String name, String email, String passwordHash) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .name(name)
                .email(email)
                .passwordHash(passwordHash)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

    public static User createFromSocial(String name, String email) {
        LocalDateTime now = LocalDateTime.now();
        return User.builder()
                .name(name)
                .email(email)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }

}
