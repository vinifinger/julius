package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSavingsRequest(
        @NotBlank(message = "Name is mandatory")
        String name,
        String colorHex,
        String icon
) {}
