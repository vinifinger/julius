package com.finance.app.web.controller;

import com.finance.app.application.usecase.ImportStatementUseCase;
import com.finance.app.domain.port.UserContext;
import com.finance.app.web.dto.response.ImportStatementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts/{accountId}/transactions")
@RequiredArgsConstructor
public class ImportTransactionController {

    private final ImportStatementUseCase importStatementUseCase;
    private final UserContext userContext;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 MB

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportStatementResponse> importTransactions(
            @PathVariable UUID accountId,
            @RequestParam(required = false) UUID defaultCategoryId,
            @RequestParam("file") MultipartFile file) {
            
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File is empty");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File exceeds maximum size of 10MB");
        }

        UUID userId = userContext.getAuthenticatedUserId();

        try {
            ImportStatementResponse response = importStatementUseCase.importStatement(
                    file.getInputStream(),
                    file.getOriginalFilename(),
                    file.getContentType(),
                    accountId,
                    defaultCategoryId,
                    userId
            );

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading file", e);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
