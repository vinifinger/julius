package com.finance.app.infrastructure.security;

import com.finance.app.domain.port.UserContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
public class MockUserContext implements UserContext {

    private static final UUID MOCK_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    @Override
    public UUID getAuthenticatedUserId() {
        return MOCK_USER_ID;
    }

}
