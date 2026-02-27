package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CreateCategoryRequest(

                @NotBlank(message = "Category name is required") String name,

                String colorHex

) {
}
