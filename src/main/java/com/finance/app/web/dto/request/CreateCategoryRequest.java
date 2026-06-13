package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.finance.app.domain.entity.TransactionType;

public record CreateCategoryRequest(

                @NotBlank(message = "Category name is required") String name,

                String colorHex,

                @NotNull(message = "Category type is required") TransactionType type

) {
}
