package com.finance.app.domain.port;

import com.finance.app.domain.entity.SocialProfile;

public interface TokenVerifier {

    SocialProfile verify(String idToken);

}
