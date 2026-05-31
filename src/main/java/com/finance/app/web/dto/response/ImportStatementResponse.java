package com.finance.app.web.dto.response;

public record ImportStatementResponse(
        int importedCount,
        int ignoredCount,
        String message
) {
}
