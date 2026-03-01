package com.finance.app.domain.port;

import java.util.UUID;

public interface UserContext {

    UUID getAuthenticatedUserId();

}
