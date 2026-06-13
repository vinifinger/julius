package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import com.finance.app.domain.entity.TransactionType;

public record UpdateCategoryRequest(

        @NotBlank(message = "Name is required")
        @Size(max = 50, message = "Name must be at most 50 characters")
        String name,

        @Pattern(regexp = "^#([A-Fa-f0-9]{6})$", message = "colorHex must follow hex format: #RRGGBB")
        String colorHex,

        @NotNull(message = "Type is required")
        TransactionType type
) {}
