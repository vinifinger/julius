package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Savings;
import com.finance.app.domain.entity.SavingsHistory;
import com.finance.app.domain.entity.SavingsHistoryType;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.SavingsNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.SavingsHistoryRepository;
import com.finance.app.domain.repository.SavingsRepository;
import com.finance.app.web.dto.request.CreateSavingsRequest;
import com.finance.app.web.dto.request.SavingsTransactionRequest;
import com.finance.app.web.dto.request.UpdateSavingsRequest;
import com.finance.app.web.dto.response.SavingsHistoryResponse;
import com.finance.app.web.dto.response.SavingsResponse;
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

    @Transactional
    public SavingsResponse deposit(UUID savingsId, SavingsTransactionRequest request, UUID userId) {
        Savings savings = savingsRepository.findById(savingsId)
                .orElseThrow(() -> new SavingsNotFoundException(savingsId));
                
        if (!savings.getUserId().equals(userId)) {
            throw new SavingsNotFoundException(savingsId);
        }

        Account account = accountRepository.findByIdAndUserId(request.accountId(), userId)
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        LocalDateTime now = LocalDateTime.now();
        
        // Deduct from account (EXPENSE-like action)
        account.updateBalance(request.amount(), TransactionType.EXPENSE);
        accountRepository.save(account);

        // Add to savings
        savings.setBalance(savings.getBalance().add(request.amount()));
        savings.setUpdatedAt(now);
        Savings updatedSavings = savingsRepository.save(savings);

        // Record history
        SavingsHistory history = SavingsHistory.builder()
                .savingsId(savings.getId())
                .accountId(account.getId())
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

        Account account = accountRepository.findByIdAndUserId(request.accountId(), userId)
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        LocalDateTime now = LocalDateTime.now();
        
        // Add to account (REVENUE-like action)
        account.updateBalance(request.amount(), TransactionType.REVENUE);
        accountRepository.save(account);

        // Deduct from savings
        savings.setBalance(savings.getBalance().subtract(request.amount()));
        savings.setUpdatedAt(now);
        Savings updatedSavings = savingsRepository.save(savings);

        // Record history
        SavingsHistory history = SavingsHistory.builder()
                .savingsId(savings.getId())
                .accountId(account.getId())
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
