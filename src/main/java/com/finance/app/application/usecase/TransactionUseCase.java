package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionSubtype;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.CategoryNotFoundException;
import com.finance.app.domain.exception.CompetenceNotFoundException;
import com.finance.app.domain.exception.DuplicateTransactionException;
import com.finance.app.domain.exception.TransactionNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.domain.service.TransactionService;
import com.finance.app.web.dto.request.CreateTransactionRequest;
import com.finance.app.web.dto.request.UpdateTransactionRequest;
import com.finance.app.web.dto.request.UpdateTransactionStatusRequest;
import com.finance.app.web.dto.response.TransactionResponse;
import com.finance.app.domain.entity.TransactionFilter;
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
        String externalId = request.externalId();

        if (externalId != null && transactionRepository.existsByExternalId(externalId)) {
            throw new DuplicateTransactionException(externalId);
        }

        Transaction transaction = Transaction.create(
                request.accountId(),
                request.categoryId(),
                request.competenceId(),
                userId,
                request.description(),
                request.amount(),
                request.dateTime(),
                type,
                request.subtype(),
                status,
                null,
                null,
                null,
                externalId);

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

    public List<TransactionResponse> listTransactions(TransactionFilter filter) {
        return transactionRepository.findByFilter(filter).stream()
                .map(TransactionResponse::fromDomain)
                .toList();
    }

    @Transactional
    public TransactionResponse update(UUID id, UpdateTransactionRequest request) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));

        if (request.accountId() != null && !request.accountId().equals(transaction.getAccountId())) {
            accountRepository.findByIdAndUserId(request.accountId(), transaction.getUserId())
                    .orElseThrow(() -> new AccountNotFoundException(request.accountId()));
        }
        if (request.categoryId() != null && !request.categoryId().equals(transaction.getCategoryId())) {
            categoryRepository.findById(request.categoryId())
                    .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));
        }
        if (request.competenceId() != null && !request.competenceId().equals(transaction.getCompetenceId())) {
            competenceRepository.findById(request.competenceId())
                    .orElseThrow(() -> new CompetenceNotFoundException(request.competenceId()));
        }

        boolean affectsBalance = request.accountId() != null || request.amount() != null 
                || request.type() != null || request.status() != null;
        
        Account oldAccount = null;
        if (transaction.isPaid() && affectsBalance) {
            oldAccount = accountRepository.findByIdAndUserId(transaction.getAccountId(), transaction.getUserId())
                    .orElseThrow(() -> new AccountNotFoundException(transaction.getAccountId()));
            transactionService.reverseTransaction(transaction, oldAccount);
        }

        if (request.accountId() != null) transaction.setAccountId(request.accountId());
        if (request.categoryId() != null) transaction.setCategoryId(request.categoryId());
        if (request.competenceId() != null) transaction.setCompetenceId(request.competenceId());
        if (request.description() != null) transaction.setDescription(request.description());
        if (request.amount() != null) transaction.setAmount(request.amount().setScale(2, java.math.RoundingMode.HALF_EVEN));
        if (request.dateTime() != null) transaction.setDateTime(request.dateTime());
        if (request.type() != null) transaction.setType(request.type());
        if (request.subtype() != null) transaction.setSubtype(request.subtype());
        if (request.status() != null) transaction.setStatus(request.status());

        if (transaction.isPaid() && affectsBalance) {
            Account newAccount = accountRepository.findByIdAndUserId(transaction.getAccountId(), transaction.getUserId())
                    .orElseThrow(() -> new AccountNotFoundException(transaction.getAccountId()));
            transactionService.processTransaction(transaction, newAccount);
            if (oldAccount != null && !oldAccount.getId().equals(newAccount.getId())) {
                accountRepository.save(oldAccount);
            }
            accountRepository.save(newAccount);
        } else if (oldAccount != null) {
            accountRepository.save(oldAccount);
        }

        transaction.setUpdatedAt(java.time.LocalDateTime.now());
        Transaction updatedTransaction = transactionRepository.save(transaction);
        
        log.atInfo().log("Updated transaction ID {} for user ID {}", id, transaction.getUserId());

        return TransactionResponse.fromDomain(updatedTransaction);
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
