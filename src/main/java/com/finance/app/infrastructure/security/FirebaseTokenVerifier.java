package com.finance.app.infrastructure.security;

import com.finance.app.domain.entity.SocialProfile;
import com.finance.app.domain.exception.InvalidFirebaseTokenException;
import com.finance.app.domain.port.TokenVerifier;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true", matchIfMissing = true)
public class FirebaseTokenVerifier implements TokenVerifier {

    @Override
    public SocialProfile verify(String idToken) {
        try {
            FirebaseToken firebaseToken = FirebaseAuth.getInstance().verifyIdToken(idToken);

            String name = firebaseToken.getName();
            String email = firebaseToken.getEmail();
            String picture = firebaseToken.getPicture();

            return new SocialProfile(name, email, picture);
        } catch (FirebaseAuthException exception) {
            throw InvalidFirebaseTokenException.invalid();
        }
    }

}
