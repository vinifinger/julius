package com.finance.app.web.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(

        @NotBlank(message = "Name is required") String name,

        @NotBlank(message = "Email is required") @Email(message = "Email must be valid") String email,

        @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters") String password

) {
}
