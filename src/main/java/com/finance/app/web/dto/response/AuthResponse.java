package com.finance.app.web.dto.response;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        String token,
        Instant expiresAt,
        UUID userId,
        String email) {
}
