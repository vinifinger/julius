package com.finance.app.infrastructure.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider("test-secret-key", 86400000L);
    }

    @Nested
    @DisplayName("generateToken")
    class GenerateToken {

        @Test
        @DisplayName("Should generate valid JWT token with correct claims")
        void givenEmailAndUserId_whenGenerateToken_thenReturnsValidToken() {
            // Given
            String email = "john@example.com";
            UUID userId = UUID.randomUUID();

            // When
            String token = jwtTokenProvider.generateToken(email, userId);

            // Then
            assertNotNull(token);

            DecodedJWT decodedJWT = jwtTokenProvider.validateToken(token);
            assertEquals(email, jwtTokenProvider.getEmail(decodedJWT));
            assertEquals(userId, jwtTokenProvider.getUserId(decodedJWT));
        }

        @Test
        @DisplayName("Should generate token with sub claim as email")
        void givenEmail_whenGenerateToken_thenTokenContainsSubject() {
            // Given
            String email = "test@example.com";
            UUID userId = UUID.randomUUID();

            // When
            String token = jwtTokenProvider.generateToken(email, userId);
            DecodedJWT decodedJWT = jwtTokenProvider.validateToken(token);

            // Then
            assertEquals(email, decodedJWT.getSubject());
        }

        @Test
        @DisplayName("Should generate token with userId custom claim")
        void givenUserId_whenGenerateToken_thenTokenContainsUserIdClaim() {
            // Given
            String email = "test@example.com";
            UUID userId = UUID.randomUUID();

            // When
            String token = jwtTokenProvider.generateToken(email, userId);
            DecodedJWT decodedJWT = jwtTokenProvider.validateToken(token);

            // Then
            assertEquals(userId.toString(), decodedJWT.getClaim("userId").asString());
        }
    }

    @Nested
    @DisplayName("validateToken")
    class ValidateToken {

        @Test
        @DisplayName("Should throw JWTVerificationException for invalid token")
        void givenInvalidToken_whenValidate_thenThrows() {
            // Given
            String invalidToken = "invalid.jwt.token";

            // When / Then
            assertThrows(JWTVerificationException.class,
                    () -> jwtTokenProvider.validateToken(invalidToken));
        }

        @Test
        @DisplayName("Should throw JWTVerificationException for expired token")
        void givenExpiredToken_whenValidate_thenThrows() {
            // Given — provider with 0ms expiration
            JwtTokenProvider expiredProvider = new JwtTokenProvider("test-secret-key", 0L);
            String token = expiredProvider.generateToken("test@example.com", UUID.randomUUID());

            // When / Then
            assertThrows(JWTVerificationException.class,
                    () -> jwtTokenProvider.validateToken(token));
        }

        @Test
        @DisplayName("Should throw JWTVerificationException for token with wrong secret")
        void givenTokenWithWrongSecret_whenValidate_thenThrows() {
            // Given
            JwtTokenProvider otherProvider = new JwtTokenProvider("different-secret", 86400000L);
            String token = otherProvider.generateToken("test@example.com", UUID.randomUUID());

            // When / Then
            assertThrows(JWTVerificationException.class,
                    () -> jwtTokenProvider.validateToken(token));
        }
    }

}
