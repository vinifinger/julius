package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.CategoryNotFoundException;
import com.finance.app.domain.exception.CompetenceNotFoundException;
import com.finance.app.domain.exception.TransactionNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.domain.service.TransactionService;
import com.finance.app.web.dto.request.CreateTransactionRequest;
import com.finance.app.web.dto.request.UpdateTransactionStatusRequest;
import com.finance.app.web.dto.response.TransactionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final CompetenceRepository competenceRepository;
    private final TransactionService transactionService;

    @Transactional
    public TransactionResponse create(CreateTransactionRequest request, UUID userId) {
        Account account = accountRepository.findByIdAndUserId(request.accountId(), userId)
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        competenceRepository.findById(request.competenceId())
                .orElseThrow(() -> new CompetenceNotFoundException(request.competenceId()));

        TransactionType type = request.type();
        TransactionStatus status = request.status();

        Transaction transaction = Transaction.create(
                request.accountId(),
                request.categoryId(),
                request.competenceId(),
                userId,
                request.description(),
                request.amount(),
                request.dateTime(),
                type,
                status);

        Transaction savedTransaction = transactionRepository.save(transaction);

        transactionService.processTransaction(savedTransaction, account);
        accountRepository.save(account);

        log.atInfo().log("Created transaction ID {} of type {} for user ID {} with amount {}", 
                savedTransaction.getId(), savedTransaction.getType(), userId, savedTransaction.getAmount());

        return TransactionResponse.fromDomain(savedTransaction);
    }

    public TransactionResponse getById(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
        return TransactionResponse.fromDomain(transaction);
    }

    public List<TransactionResponse> listByUser(UUID userId) {
        return transactionRepository.findByUserId(userId).stream()
                .map(TransactionResponse::fromDomain)
                .toList();
    }

    @Transactional
    public TransactionResponse updateStatus(UUID id, UpdateTransactionStatusRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        Account account = accountRepository.findByIdAndUserId(transaction.getAccountId(), transaction.getUserId())
                .orElseThrow(() -> new AccountNotFoundException(transaction.getAccountId()));

        TransactionStatus newStatus = request.status();
        TransactionStatus oldStatus = transaction.getStatus();

        if (oldStatus.equals(newStatus)) {
            return TransactionResponse.fromDomain(transaction);
        }

        if (TransactionStatus.PENDING.equals(oldStatus) && TransactionStatus.PAID.equals(newStatus)) {
            transaction.setStatus(TransactionStatus.PAID);
            transactionService.processTransaction(transaction, account);
        } else if (TransactionStatus.PAID.equals(oldStatus) && TransactionStatus.PENDING.equals(newStatus)) {
            transactionService.reverseTransaction(transaction, account);
            transaction.setStatus(TransactionStatus.PENDING);
        }

        Transaction updatedTransaction = transactionRepository.save(transaction);
        accountRepository.save(account);

        log.atInfo().log("Updated transaction ID {} status from {} to {} for user ID {}", 
                id, oldStatus, transaction.getStatus(), transaction.getUserId());

        return TransactionResponse.fromDomain(updatedTransaction);
    }

    @Transactional
    public void delete(UUID id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        if (transaction.isPaid()) {
            Account account = accountRepository.findByIdAndUserId(transaction.getAccountId(), transaction.getUserId())
                    .orElseThrow(() -> new AccountNotFoundException(transaction.getAccountId()));
            transactionService.reverseTransaction(transaction, account);
            accountRepository.save(account);
        }

        log.atInfo().log("Deleting transaction ID {} for user ID {}", id, transaction.getUserId());
        transactionRepository.delete(id);
    }


}
