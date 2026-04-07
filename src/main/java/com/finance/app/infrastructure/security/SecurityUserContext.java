package com.finance.app.infrastructure.security;

import com.finance.app.domain.port.UserContext;
import com.finance.app.domain.exception.UnauthenticatedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class SecurityUserContext implements UserContext {

    @Override
    public UUID getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (Objects.isNull(authentication) || !authentication.isAuthenticated()) {
            throw new UnauthenticatedException();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof UUID uuid) {
            return uuid;
        }

        if (principal instanceof String stringPrincipal) {
            try {
                return UUID.fromString(stringPrincipal);
            } catch (IllegalArgumentException e) {
                // Handle cases like "anonymousUser" or other string principals
                throw new UnauthenticatedException("Invalid principal type: " + stringPrincipal);
            }
        }

        throw new UnauthenticatedException("Unknown principal type: " + principal.getClass().getName());
    }

}
