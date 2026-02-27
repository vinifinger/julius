package com.finance.app.web.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateCompetenceRequest(

        @NotNull(message = "Month is required") @Min(value = 1, message = "Month must be between 1 and 12") @Max(value = 12, message = "Month must be between 1 and 12") Integer month,

        @NotNull(message = "Year is required") @Min(value = 2000, message = "Year must be 2000 or later") Integer year

) {
}
