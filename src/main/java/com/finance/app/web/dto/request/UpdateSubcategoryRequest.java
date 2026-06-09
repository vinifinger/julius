package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateSubcategoryRequest(
        @NotBlank(message = "Name is mandatory")
        String name
) {}
