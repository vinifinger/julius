package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Category;
import com.finance.app.domain.entity.Savings;
import com.finance.app.domain.entity.SavingsHistory;
import com.finance.app.domain.entity.SavingsHistoryType;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.SavingsNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.SavingsHistoryRepository;
import com.finance.app.domain.repository.SavingsRepository;
import com.finance.app.web.dto.request.CreateSavingsRequest;
import com.finance.app.web.dto.request.CreateTransactionRequest;
import com.finance.app.web.dto.request.SavingsTransactionRequest;
import com.finance.app.web.dto.request.UpdateSavingsRequest;
import com.finance.app.web.dto.response.SavingsHistoryResponse;
import com.finance.app.web.dto.response.SavingsResponse;
import com.finance.app.web.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavingsUseCase {

    private final SavingsRepository savingsRepository;
    private final SavingsHistoryRepository savingsHistoryRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final TransactionUseCase transactionUseCase;

    private static final String SAVINGS_CATEGORY_NAME = "Savings Vault";
    private static final String SAVINGS_CATEGORY_COLOR = "#FFD700"; // Gold color

    @Transactional
    public SavingsResponse create(CreateSavingsRequest request, UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        BigDecimal initialBalance = request.initialBalance() != null ? request.initialBalance() : BigDecimal.ZERO;
        
        Savings savings = Savings.builder()
                .userId(userId)
                .name(request.name())
                .balance(initialBalance)
                .colorHex(request.colorHex())
                .icon(request.icon())
                .createdAt(now)
                .updatedAt(now)
                .build();
                
        Savings savedSavings = savingsRepository.save(savings);
        return SavingsResponse.fromDomain(savedSavings);
    }

    @Transactional
    public SavingsResponse update(UUID id, UpdateSavingsRequest request, UUID userId) {
        Savings savings = savingsRepository.findById(id)
                .orElseThrow(() -> new SavingsNotFoundException(id));
                
        if (!savings.getUserId().equals(userId)) {
            throw new SavingsNotFoundException(id);
        }

        savings.setName(request.name());
        savings.setColorHex(request.colorHex());
        savings.setIcon(request.icon());
        savings.setUpdatedAt(LocalDateTime.now());

        Savings updatedSavings = savingsRepository.save(savings);
        return SavingsResponse.fromDomain(updatedSavings);
    }

    public List<SavingsResponse> listByUser(UUID userId) {
        return savingsRepository.findByUserId(userId).stream()
                .map(SavingsResponse::fromDomain)
                .collect(Collectors.toList());
    }

    public SavingsResponse getById(UUID id, UUID userId) {
        Savings savings = savingsRepository.findById(id)
                .orElseThrow(() -> new SavingsNotFoundException(id));
                
        if (!savings.getUserId().equals(userId)) {
            throw new SavingsNotFoundException(id);
        }
        
        return SavingsResponse.fromDomain(savings);
    }

    private Category getOrCreateSavingsCategory(UUID userId, TransactionType type) {
        return categoryRepository.findByUserIdAndNameAndType(userId, SAVINGS_CATEGORY_NAME, type)
                .orElseGet(() -> {
                    Category newCategory = Category.builder()
                            .userId(userId)
                            .name(SAVINGS_CATEGORY_NAME)
                            .colorHex(SAVINGS_CATEGORY_COLOR)
                            .type(type)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return categoryRepository.save(newCategory);
                });
    }

    @Transactional
    public SavingsResponse deposit(UUID savingsId, SavingsTransactionRequest request, UUID userId) {
        Savings savings = savingsRepository.findById(savingsId)
                .orElseThrow(() -> new SavingsNotFoundException(savingsId));
                
        if (!savings.getUserId().equals(userId)) {
            throw new SavingsNotFoundException(savingsId);
        }

        Category savingsCategory = getOrCreateSavingsCategory(userId, TransactionType.EXPENSE);
        LocalDateTime now = LocalDateTime.now();

        // Create transaction to deduct from account balance and associate with competence
        CreateTransactionRequest txRequest = new CreateTransactionRequest(
                request.accountId(),
                savingsCategory.getId(),
                null,
                request.competenceId(),
                request.description() != null ? request.description() : "Transfer to Savings",
                request.amount(),
                now,
                TransactionType.EXPENSE,
                null,
                TransactionStatus.COMPLETED,
                null
        );

        TransactionResponse txResponse = transactionUseCase.create(txRequest, userId);

        // Add to savings
        savings.setBalance(savings.getBalance().add(request.amount()));
        savings.setUpdatedAt(now);
        Savings updatedSavings = savingsRepository.save(savings);

        // Record history
        SavingsHistory history = SavingsHistory.builder()
                .savingsId(savings.getId())
                .accountId(request.accountId())
                .transactionId(txResponse.id())
                .type(SavingsHistoryType.DEPOSIT)
                .amount(request.amount())
                .description(request.description())
                .createdAt(now)
                .build();
        savingsHistoryRepository.save(history);

        return SavingsResponse.fromDomain(updatedSavings);
    }

    @Transactional
    public SavingsResponse withdraw(UUID savingsId, SavingsTransactionRequest request, UUID userId) {
        Savings savings = savingsRepository.findById(savingsId)
                .orElseThrow(() -> new SavingsNotFoundException(savingsId));
                
        if (!savings.getUserId().equals(userId)) {
            throw new SavingsNotFoundException(savingsId);
        }
        
        if (savings.getBalance().compareTo(request.amount()) < 0) {
            throw new IllegalArgumentException("Insufficient funds in savings vault");
        }

        Category savingsCategory = getOrCreateSavingsCategory(userId, TransactionType.REVENUE);
        LocalDateTime now = LocalDateTime.now();

        // Create transaction to add to account balance and associate with competence
        CreateTransactionRequest txRequest = new CreateTransactionRequest(
                request.accountId(),
                savingsCategory.getId(),
                null,
                request.competenceId(),
                request.description() != null ? request.description() : "Withdraw from Savings",
                request.amount(),
                now,
                TransactionType.REVENUE,
                null,
                TransactionStatus.COMPLETED,
                null
        );

        TransactionResponse txResponse = transactionUseCase.create(txRequest, userId);

        // Deduct from savings
        savings.setBalance(savings.getBalance().subtract(request.amount()));
        savings.setUpdatedAt(now);
        Savings updatedSavings = savingsRepository.save(savings);

        // Record history
        SavingsHistory history = SavingsHistory.builder()
                .savingsId(savings.getId())
                .accountId(request.accountId())
                .transactionId(txResponse.id())
                .type(SavingsHistoryType.WITHDRAWAL)
                .amount(request.amount())
                .description(request.description())
                .createdAt(now)
                .build();
        savingsHistoryRepository.save(history);

        return SavingsResponse.fromDomain(updatedSavings);
    }

    public List<SavingsHistoryResponse> getHistory(UUID savingsId, UUID userId) {
        Savings savings = savingsRepository.findById(savingsId)
                .orElseThrow(() -> new SavingsNotFoundException(savingsId));
                
        if (!savings.getUserId().equals(userId)) {
            throw new SavingsNotFoundException(savingsId);
        }
        
        return savingsHistoryRepository.findBySavingsIdOrderByCreatedAtDesc(savingsId).stream()
                .map(SavingsHistoryResponse::fromDomain)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(UUID id, UUID userId) {
        Savings savings = savingsRepository.findById(id)
                .orElseThrow(() -> new SavingsNotFoundException(id));
                
        if (!savings.getUserId().equals(userId)) {
            throw new SavingsNotFoundException(id);
        }
        
        savingsRepository.deleteById(id);
    }
}
