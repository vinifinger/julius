package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must be at most 50 characters")
        String name,

        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "colorHex must follow hex format: #RRGGBB")
        String colorHex
) {}
