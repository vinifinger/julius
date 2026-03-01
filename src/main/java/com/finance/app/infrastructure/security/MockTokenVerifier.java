package com.finance.app.infrastructure.security;

import com.finance.app.domain.entity.SocialProfile;
import com.finance.app.domain.port.TokenVerifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "false")
public class MockTokenVerifier implements TokenVerifier {

    @Override
    public SocialProfile verify(String idToken) {
        return new SocialProfile("Mock User", "mock@example.com", "https://mock.url/photo.jpg");
    }

}
