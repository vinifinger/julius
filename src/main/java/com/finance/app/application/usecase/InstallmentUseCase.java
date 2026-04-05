package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.InstallmentSeries;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.CategoryNotFoundException;
import com.finance.app.domain.exception.CompetenceNotFoundException;
import com.finance.app.domain.exception.InstallmentValidationException;
import com.finance.app.domain.exception.InvalidTransactionException;
import com.finance.app.domain.exception.TransactionNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.domain.service.TransactionService;
import com.finance.app.web.dto.request.CreateInstallmentRequest;
import com.finance.app.web.dto.request.UpdateInstallmentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstallmentUseCase {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CategoryRepository categoryRepository;
    private final CompetenceRepository competenceRepository;
    private final TransactionService transactionService;

    @Transactional
    public InstallmentSeries createInstallmentSeries(CreateInstallmentRequest request, UUID userId) {
        if (request.installments() < 2) {
            throw new InstallmentValidationException("Number of installments must be at least 2");
        }

        BigDecimal totalAmount;
        BigDecimal installmentAmount;

        if (Objects.nonNull(request.totalAmount()) && Objects.isNull(request.installmentAmount())) {
            totalAmount = request.totalAmount().setScale(2, RoundingMode.HALF_EVEN);
            installmentAmount = totalAmount.divide(BigDecimal.valueOf(request.installments()), 2, RoundingMode.HALF_EVEN);
        } else if (Objects.isNull(request.totalAmount()) && Objects.nonNull(request.installmentAmount())) {
            installmentAmount = request.installmentAmount().setScale(2, RoundingMode.HALF_EVEN);
            totalAmount = installmentAmount.multiply(BigDecimal.valueOf(request.installments())).setScale(2, RoundingMode.HALF_EVEN);
        } else if (Objects.nonNull(request.totalAmount()) && Objects.nonNull(request.installmentAmount())) {
            totalAmount = request.totalAmount().setScale(2, RoundingMode.HALF_EVEN);
            installmentAmount = request.installmentAmount().setScale(2, RoundingMode.HALF_EVEN);
            BigDecimal calculatedTotal = installmentAmount.multiply(BigDecimal.valueOf(request.installments()));
            BigDecimal diff = totalAmount.subtract(calculatedTotal).abs();
            if (diff.compareTo(BigDecimal.valueOf(request.installments()).multiply(new BigDecimal("0.01"))) > 0) {
                 throw new InstallmentValidationException("Divergence between total amount and installment amount * n");
            }
        } else {
            throw new InstallmentValidationException("Either totalAmount or installmentAmount must be provided");
        }

        Account account = accountRepository.findByIdAndUserId(request.accountId(), userId)
                .orElseThrow(() -> new AccountNotFoundException(request.accountId()));

        categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new CategoryNotFoundException(request.categoryId()));

        Competence initialCompetence = competenceRepository.findById(request.competenceId())
                .orElseThrow(() -> new CompetenceNotFoundException(request.competenceId()));

        TransactionType type = parseTransactionType(request.type());
        TransactionStatus status = parseTransactionStatus(request.status());

        List<Transaction> transactionsToSave = new ArrayList<>();
        UUID parentId = UUID.randomUUID(); // Predetermining parent ID
        
        int currentMonth = initialCompetence.getMonth();
        int currentYear = initialCompetence.getYear();

        BigDecimal sumOfInstallments = BigDecimal.ZERO;

        for (int i = 1; i <= request.installments(); i++) {
            BigDecimal currentInstallmentAmount;
            
            if (i == request.installments()) {
                currentInstallmentAmount = totalAmount.subtract(sumOfInstallments);
                log.atInfo().log("Applying residue to last installment. Target Total: {}, Sum so far: {}, Last Installment: {}", totalAmount, sumOfInstallments, currentInstallmentAmount);
            } else {
                currentInstallmentAmount = installmentAmount;
                sumOfInstallments = sumOfInstallments.add(currentInstallmentAmount);
            }

            Competence competence = getOrCreateCompetence(userId, currentMonth, currentYear);
            
            Transaction transaction = Transaction.create(
                    request.accountId(),
                    request.categoryId(),
                    competence.getId(),
                    userId,
                    request.description(),
                    currentInstallmentAmount,
                    request.dateTime(),
                    type,
                    status,
                    parentId, // Assign the pre-generated parentId to all, including the parent itself (or we structure differently)
                    request.installments(),
                    i
            );
            
            if (i == 1) {
                // Set the exact ID for the parent transaction
                transaction.setId(parentId);
            }

            transactionsToSave.add(transaction);

            if (TransactionStatus.PAID.equals(status)) {
                 transactionService.processTransaction(transaction, account);
            }

            currentMonth++;
            if (currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
        }

        List<Transaction> savedTransactions = transactionRepository.saveAll(transactionsToSave);
        accountRepository.save(account);

        return buildSeriesProjection(savedTransactions);
    }

    public InstallmentSeries getInstallmentProgress(UUID parentId) {
         List<Transaction> transactions = transactionRepository.findByParentId(parentId);
         if (transactions.isEmpty()) {
             throw new TransactionNotFoundException(parentId);
         }
         return buildSeriesProjection(transactions);
    }

    @Transactional
    public InstallmentSeries updateInstallmentSeries(UUID parentId, UpdateInstallmentRequest request) {
        List<Transaction> transactions = transactionRepository.findByParentId(parentId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(parentId);
        }

        List<Transaction> paidInstallments = transactions.stream()
                .filter(Transaction::isPaid)
                .toList();
        
        List<Transaction> pendingInstallments = transactions.stream()
                .filter(t -> !t.isPaid())
                .sorted(Comparator.comparingInt(Transaction::getInstallmentNumber))
                .toList();

        if (pendingInstallments.isEmpty()) {
             throw new InstallmentValidationException("Cannot update series where all installments are paid");
        }

        BigDecimal paidTotal = paidInstallments.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal newTotal = request.newTotalAmount().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal newPendingTotal = newTotal.subtract(paidTotal);

        if (newPendingTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InstallmentValidationException("New total amount must be greater than the already paid amount (" + paidTotal + ")");
        }

        BigDecimal newInstallmentAmount = newPendingTotal.divide(BigDecimal.valueOf(pendingInstallments.size()), 2, RoundingMode.HALF_EVEN);
        BigDecimal sumOfNewPending = BigDecimal.ZERO;

        for (int i = 0; i < pendingInstallments.size(); i++) {
            Transaction transaction = pendingInstallments.get(i);
            BigDecimal currentAmount;

            if (i == pendingInstallments.size() - 1) {
                currentAmount = newPendingTotal.subtract(sumOfNewPending);
            } else {
                 currentAmount = newInstallmentAmount;
                 sumOfNewPending = sumOfNewPending.add(currentAmount);
            }

            transaction.setAmount(currentAmount);
            transactionRepository.save(transaction);
        }

        return buildSeriesProjection(transactions); // Returning with updated values
    }

    @Transactional
    public InstallmentSeries changeInstallmentType(UUID parentId, String newTypeStr) {
        TransactionType newType = parseTransactionType(newTypeStr);

        List<Transaction> transactions = transactionRepository.findByParentId(parentId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(parentId);
        }

        Transaction first = transactions.get(0);
        if (first.getType().equals(newType)) {
             return buildSeriesProjection(transactions);
        }

        Account account = accountRepository.findByIdAndUserId(first.getAccountId(), first.getUserId())
                 .orElseThrow(() -> new AccountNotFoundException(first.getAccountId()));

        for (Transaction transaction : transactions) {
            if (transaction.isPaid()) {
                 transactionService.reverseTransaction(transaction, account); // Reverse old effect
                 transaction.setType(newType);
                 transactionService.processTransaction(transaction, account); // Apply new effect
            } else {
                 transaction.setType(newType);
            }
        }

        transactionRepository.saveAll(transactions);
        accountRepository.save(account);

        return buildSeriesProjection(transactions);
    }


    private Competence getOrCreateCompetence(UUID userId, int month, int year) {
        return competenceRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .orElseGet(() -> {
                    LocalDateTime now = LocalDateTime.now();
                    Competence newCompetence = Competence.builder()
                            .userId(userId)
                            .month(month)
                            .year(year)
                            .createdAt(now)
                            .updatedAt(now)
                            .build();
                    return competenceRepository.save(newCompetence);
                });
    }

    private InstallmentSeries buildSeriesProjection(List<Transaction> seriesTrans) {
        if (seriesTrans.isEmpty()) {
             return null;
        }

        Transaction first = seriesTrans.get(0); // Using first to get parentId/desc etc
        UUID parentId = first.getParentId() != null ? first.getParentId() : first.getId();

        int totalInstallments = seriesTrans.size();
        int paidCount = 0;
        int pendingCount = 0;
        BigDecimal paidSum = BigDecimal.ZERO;
        BigDecimal pendingSum = BigDecimal.ZERO;

        for (Transaction t : seriesTrans) {
             if (t.isPaid()) {
                 paidCount++;
                 paidSum = paidSum.add(t.getAmount());
             } else {
                 pendingCount++;
                 pendingSum = pendingSum.add(t.getAmount());
             }
        }

        return new InstallmentSeries(
                parentId,
                first.getDescription(),
                paidSum.add(pendingSum),
                totalInstallments,
                paidCount,
                pendingCount,
                paidSum,
                pendingSum
        );
    }
    
    private TransactionType parseTransactionType(String type) {
        try {
            return TransactionType.valueOf(type.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidTransactionException("Invalid transaction type: " + type + ". Must be REVENUE or EXPENSE");
        }
    }

    private TransactionStatus parseTransactionStatus(String status) {
        try {
            return TransactionStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new InvalidTransactionException(
                    "Invalid transaction status: " + status + ". Must be PENDING or PAID");
        }
    }
}
