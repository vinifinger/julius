package com.finance.app.application.usecase;

import com.finance.app.domain.entity.SocialProfile;
import com.finance.app.domain.entity.User;
import com.finance.app.domain.exception.DuplicateEmailException;
import com.finance.app.domain.exception.InvalidCredentialsException;
import com.finance.app.domain.exception.InvalidFirebaseTokenException;
import com.finance.app.domain.port.TokenVerifier;
import com.finance.app.domain.repository.UserRepository;
import com.finance.app.infrastructure.security.JwtTokenProvider;
import com.finance.app.web.dto.request.GoogleAuthRequest;
import com.finance.app.web.dto.request.LoginRequest;
import com.finance.app.web.dto.request.RegisterRequest;
import com.finance.app.web.dto.response.AuthResponse;
import com.finance.app.web.dto.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenVerifier tokenVerifier;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("Should register user with encrypted password")
        void givenValidRequest_whenRegister_thenReturnsUserResponseWithEncryptedPassword() {
            // Given
            RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123");
            when(userRepository.existsByEmail(request.email())).thenReturn(false);
            when(passwordEncoder.encode("password123")).thenReturn("$2a$10$hashedpassword");
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(UUID.randomUUID());
                return user;
            });

            // When
            UserResponse response = authUseCase.register(request);

            // Then
            assertNotNull(response);
            assertNotNull(response.id());
            assertEquals("John Doe", response.name());
            assertEquals("john@example.com", response.email());
            verify(passwordEncoder).encode("password123");
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw DuplicateEmailException when email already exists")
        void givenDuplicateEmail_whenRegister_thenThrowsDuplicateEmailException() {
            // Given
            RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "password123");
            when(userRepository.existsByEmail(request.email())).thenReturn(true);

            // When / Then
            DuplicateEmailException exception = assertThrows(DuplicateEmailException.class,
                    () -> authUseCase.register(request));

            assertEquals("Email already in use: john@example.com", exception.getMessage());
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("login")
    class Login {

        @Test
        @DisplayName("Should return auth response with JWT token on valid credentials")
        void givenValidCredentials_whenLogin_thenReturnsAuthResponse() {
            // Given
            UUID userId = UUID.randomUUID();
            LoginRequest request = new LoginRequest("john@example.com", "password123");
            User user = User.builder()
                    .id(userId)
                    .name("John Doe")
                    .email("john@example.com")
                    .passwordHash("$2a$10$hashedpassword")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(true);
            when(jwtTokenProvider.generateToken("john@example.com", userId)).thenReturn("jwt-token-value");
            when(jwtTokenProvider.getExpiresAt("jwt-token-value")).thenReturn(Instant.now().plusSeconds(86400));

            // When
            AuthResponse response = authUseCase.login(request);

            // Then
            assertNotNull(response);
            assertEquals("jwt-token-value", response.token());
            assertEquals(userId, response.userId());
            assertEquals("john@example.com", response.email());
            assertNotNull(response.expiresAt());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when email not found")
        void givenInvalidEmail_whenLogin_thenThrowsInvalidCredentials() {
            // Given
            LoginRequest request = new LoginRequest("unknown@example.com", "password123");
            when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            // When / Then
            assertThrows(InvalidCredentialsException.class, () -> authUseCase.login(request));
            verify(passwordEncoder, never()).matches(anyString(), anyString());
        }

        @Test
        @DisplayName("Should throw InvalidCredentialsException when password is wrong")
        void givenWrongPassword_whenLogin_thenThrowsInvalidCredentials() {
            // Given
            LoginRequest request = new LoginRequest("john@example.com", "wrongpassword");
            User user = User.builder()
                    .id(UUID.randomUUID())
                    .email("john@example.com")
                    .passwordHash("$2a$10$hashedpassword")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongpassword", "$2a$10$hashedpassword")).thenReturn(false);

            // When / Then
            assertThrows(InvalidCredentialsException.class, () -> authUseCase.login(request));
            verify(jwtTokenProvider, never()).generateToken(anyString(), any(UUID.class));
        }
    }

    @Nested
    @DisplayName("authenticateGoogle")
    class AuthenticateGoogle {

        @Test
        @DisplayName("Should create shadow user and return JWT on first Google login")
        void givenNewGoogleUser_whenAuthenticate_thenCreatesShadowUserAndReturnsToken() {
            // Given
            GoogleAuthRequest request = new GoogleAuthRequest("firebase-id-token");
            SocialProfile profile = new SocialProfile("Google User", "google@example.com", "https://photo.url");
            UUID userId = UUID.randomUUID();

            when(tokenVerifier.verify("firebase-id-token")).thenReturn(profile);
            when(userRepository.findByEmail("google@example.com")).thenReturn(Optional.empty());
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
                User user = invocation.getArgument(0);
                user.setId(userId);
                return user;
            });
            when(jwtTokenProvider.generateToken("google@example.com", userId)).thenReturn("jwt-token");
            when(jwtTokenProvider.getExpiresAt("jwt-token")).thenReturn(Instant.now().plusSeconds(86400));

            // When
            AuthResponse response = authUseCase.authenticateGoogle(request);

            // Then
            assertNotNull(response);
            assertEquals("jwt-token", response.token());
            assertEquals(userId, response.userId());
            assertEquals("google@example.com", response.email());
            verify(userRepository).save(any(User.class));
        }

        @Test
        @DisplayName("Should return existing user and JWT on subsequent Google login")
        void givenExistingGoogleUser_whenAuthenticate_thenReturnsExistingUserToken() {
            // Given
            UUID userId = UUID.randomUUID();
            GoogleAuthRequest request = new GoogleAuthRequest("firebase-id-token");
            SocialProfile profile = new SocialProfile("Google User", "google@example.com", "https://photo.url");
            User existingUser = User.builder()
                    .id(userId)
                    .name("Google User")
                    .email("google@example.com")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            when(tokenVerifier.verify("firebase-id-token")).thenReturn(profile);
            when(userRepository.findByEmail("google@example.com")).thenReturn(Optional.of(existingUser));
            when(jwtTokenProvider.generateToken("google@example.com", userId)).thenReturn("jwt-token");
            when(jwtTokenProvider.getExpiresAt("jwt-token")).thenReturn(Instant.now().plusSeconds(86400));

            // When
            AuthResponse response = authUseCase.authenticateGoogle(request);

            // Then
            assertNotNull(response);
            assertEquals(userId, response.userId());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        @DisplayName("Should throw InvalidFirebaseTokenException when token is invalid")
        void givenInvalidFirebaseToken_whenAuthenticate_thenThrowsException() {
            // Given
            GoogleAuthRequest request = new GoogleAuthRequest("invalid-token");
            when(tokenVerifier.verify("invalid-token")).thenThrow(InvalidFirebaseTokenException.invalid());

            // When / Then
            assertThrows(InvalidFirebaseTokenException.class,
                    () -> authUseCase.authenticateGoogle(request));
            verify(userRepository, never()).findByEmail(anyString());
        }
    }

}
