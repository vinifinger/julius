package com.finance.app.web.dto.request;

import jakarta.validation.constraints.NotNull;

public record UpdateTransactionStatusRequest(

        @NotNull(message = "Status is required") String status

) {
}
