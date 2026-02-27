package com.finance.app.application.usecase;

import com.finance.app.domain.entity.User;
import com.finance.app.domain.exception.DuplicateEmailException;
import com.finance.app.domain.exception.UserNotFoundException;
import com.finance.app.domain.repository.UserRepository;
import com.finance.app.web.dto.request.CreateUserRequest;
import com.finance.app.web.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserUseCase userUseCase;

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("Should create user successfully when email is not taken")
        void givenValidRequest_whenCreate_thenReturnsUserResponse() {
            // Given
            CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "password123");
            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(UUID.randomUUID());
                return user;
            });

            // When
            UserResponse response = userUseCase.create(request);

            // Then
            assertNotNull(response);
            assertNotNull(response.id());
            assertEquals(request.name(), response.name());
            assertEquals(request.email(), response.email());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email already exists")
        void givenDuplicateEmail_whenCreate_thenThrowsDuplicateEmailException() {
            // Given
            CreateUserRequest request = new CreateUserRequest("John Doe", "john@example.com", "password123");
            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            // When / Then
            DuplicateEmailException exception = assertThrows(DuplicateEmailException.class,
                    () -> userUseCase.create(request));

            assertEquals("Email already in use: john@example.com", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("getById")
    class GetById {

        @Test
        @DisplayName("Should return user when found by ID")
        void givenExistingId_whenGetById_thenReturnsUserResponse() {
            // Given
            UUID userId = UUID.randomUUID();
            LocalDateTime now = LocalDateTime.now();
            User user = User.builder()
                    .id(userId)
                    .name("Jane Doe")
                    .email("jane@example.com")
                    .passwordHash("hashed_password")
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));

            // When
            UserResponse response = userUseCase.getById(userId);

            // Then
            assertNotNull(response);
            assertEquals(userId, response.id());
            assertEquals("Jane Doe", response.name());
            assertEquals("jane@example.com", response.email());
        }

        @Test
        @DisplayName("Should throw UserNotFoundException when user does not exist")
        void givenNonExistingId_whenGetById_thenThrowsUserNotFoundException() {
            // Given
            UUID userId = UUID.randomUUID();
            when(userRepository.findById(userId)).thenReturn(Optional.empty());

            // When / Then
            UserNotFoundException exception = assertThrows(UserNotFoundException.class,
                    () -> userUseCase.getById(userId));

            assertEquals("User not found with id: " + userId, exception.getMessage());
        }
    }

    @Nested
    @DisplayName("listAll")
    class ListAll {

        @Test
        @DisplayName("Should return list of all users")
        void givenUsersExist_whenListAll_thenReturnsUserResponses() {
            // Given
            LocalDateTime now = LocalDateTime.now();
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .name("John Doe")
                    .email("john@example.com")
                    .passwordHash("hashed_password")
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            when(userRepository.findAll()).thenReturn(List.of(user));

            // When
            List<UserResponse> responses = userUseCase.listAll();

            // Then
            assertEquals(1, responses.size());
            assertEquals("John Doe", responses.get(0).name());
        }

        @Test
        @DisplayName("Should return empty list when no users exist")
        void givenNoUsers_whenListAll_thenReturnsEmptyList() {
            // Given
            when(userRepository.findAll()).thenReturn(List.of());

            // When
            List<UserResponse> responses = userUseCase.listAll();

            // Then
            assertTrue(responses.isEmpty());
        }
    }

}
