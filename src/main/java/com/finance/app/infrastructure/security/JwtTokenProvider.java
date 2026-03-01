package com.finance.app.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final JWTVerifier verifier;
    private final long expirationMs;

    public JwtTokenProvider(@Value("${app.security.jwt-secret:julius-jwt-secret-key}") String secret,
            @Value("${app.security.jwt-expiration-ms:86400000}") long expirationMs) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.verifier = JWT.require(algorithm).build();
        this.expirationMs = expirationMs;
    }

    public String generateToken(String email, UUID userId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMs);

        return JWT.create()
                .withSubject(email)
                .withClaim("userId", userId.toString())
                .withIssuedAt(now)
                .withExpiresAt(expiresAt)
                .sign(algorithm);
    }

    public DecodedJWT validateToken(String token) throws JWTVerificationException {
        return verifier.verify(token);
    }

    public String getEmail(DecodedJWT decodedJWT) {
        return decodedJWT.getSubject();
    }

    public UUID getUserId(DecodedJWT decodedJWT) {
        return UUID.fromString(decodedJWT.getClaim("userId").asString());
    }

    public Instant getExpiresAt(String token) {
        DecodedJWT decodedJWT = verifier.verify(token);
        return decodedJWT.getExpiresAtAsInstant();
    }

}
