package com.finance.app.web.dto.request;

import com.finance.app.domain.entity.TransactionStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateTransactionStatusRequest(

        @NotNull(message = "Status is required") TransactionStatus status

) {
}
