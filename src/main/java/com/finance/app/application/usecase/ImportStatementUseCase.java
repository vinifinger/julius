package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Category;
import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.ParsedTransaction;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.CategoryNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.domain.service.StatementParser;
import com.finance.app.web.dto.request.CreateTransactionRequest;
import com.finance.app.web.dto.response.ImportStatementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImportStatementUseCase {

    private final List<StatementParser> parsers;
    private final TransactionUseCase transactionUseCase;
    private final CategoryUseCase categoryUseCase;
    private final CompetenceUseCase competenceUseCase;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;

    public ImportStatementResponse importStatement(InputStream inputStream, String fileName, String contentType, 
                                                   UUID accountId, UUID defaultCategoryId, UUID userId) {
        
        // 1. Verify Account Ownership
        accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        // 2. Select Category (provided or system uncategorized)
        UUID finalCategoryId = resolveCategoryId(defaultCategoryId, userId);

        // 3. Find Parser
        StatementParser parser = parsers.stream()
                .filter(p -> p.supports(fileName, contentType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported file type or no parser found for: " + fileName));

        // 4. Parse File
        List<ParsedTransaction> parsedTransactions = parser.parse(inputStream);
        log.info("Parsed {} transactions from file {}", parsedTransactions.size(), fileName);

        int importedCount = 0;
        int ignoredCount = 0;

        // 5. Process Transactions
        for (ParsedTransaction pt : parsedTransactions) {
            // Deduplication
            if (pt.externalId() != null && transactionRepository.existsByExternalId(pt.externalId())) {
                ignoredCount++;
                continue;
            }

            // Resolve Competence
            Competence competence = competenceUseCase.getOrCreate(pt.dateTime().getMonthValue(), pt.dateTime().getYear(), userId);

            // Create Request
            CreateTransactionRequest request = new CreateTransactionRequest(
                    accountId,
                    finalCategoryId,
                    null,
                    competence.getId(),
                    pt.description(),
                    pt.amount(),
                    pt.dateTime(),
                    pt.type(),
                    null,
                    TransactionStatus.COMPLETED,
                    pt.externalId()
            );

            // Using TransactionUseCase to apply business rules and affect account balance
            transactionUseCase.create(request, userId);
            importedCount++;
        }

        String message = String.format("Import completed. %d imported, %d ignored (duplicates).", importedCount, ignoredCount);
        log.info(message);

        return new ImportStatementResponse(importedCount, ignoredCount, message);
    }

    private UUID resolveCategoryId(UUID defaultCategoryId, UUID userId) {
        if (defaultCategoryId != null) {
            categoryRepository.findById(defaultCategoryId)
                    .orElseThrow(() -> new CategoryNotFoundException(defaultCategoryId));
            return defaultCategoryId;
        } else {
            Category category = categoryUseCase.getOrCreateUncategorized(userId);
            return category.getId();
        }
    }
}
