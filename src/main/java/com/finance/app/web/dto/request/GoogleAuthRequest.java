package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record GoogleAuthRequest(

        @NotBlank(message = "Firebase ID token is required") String idToken

) {
}
