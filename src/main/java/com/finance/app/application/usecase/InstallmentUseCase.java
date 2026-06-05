package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Account;
import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.InstallmentSeries;
import com.finance.app.domain.entity.Transaction;
import com.finance.app.domain.entity.TransactionSubtype;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.AccountNotFoundException;
import com.finance.app.domain.exception.CategoryNotFoundException;
import com.finance.app.domain.exception.CompetenceNotFoundException;
import com.finance.app.domain.exception.InstallmentValidationException;
import com.finance.app.domain.exception.TransactionNotFoundException;
import com.finance.app.domain.repository.AccountRepository;
import com.finance.app.domain.repository.CategoryRepository;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.domain.service.InstallmentCalculator;
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
        InstallmentCalculator.validateInstallmentCount(request.installments());
        
        BigDecimal totalAmount = resolveTotalAmount(request);
        BigDecimal installmentAmount = resolveInstallmentAmount(request, totalAmount);
        
        if (Objects.nonNull(request.totalAmount()) && Objects.nonNull(request.installmentAmount())) {
            InstallmentCalculator.validateHybridInput(totalAmount, installmentAmount, request.installments());
        }

        Account account = findAccount(request.accountId(), userId);
        validateCategory(request.categoryId());
        Competence initialCompetence = findCompetence(request.competenceId());

        Transaction rootTransaction = generateRootTransaction(request, userId, totalAmount, installmentAmount, initialCompetence, account);
        Transaction savedRoot = transactionRepository.save(rootTransaction);

        List<Transaction> children = generateChildren(request, userId, totalAmount, installmentAmount, initialCompetence, account, savedRoot.getId());
        List<Transaction> savedChildren = transactionRepository.saveAll(children);

        accountRepository.save(account);

        List<Transaction> allSaved = new ArrayList<>();
        allSaved.add(savedRoot);
        allSaved.addAll(savedChildren);

        return InstallmentSeries.fromTransactions(allSaved);
    }

    private Transaction generateRootTransaction(CreateInstallmentRequest request, UUID userId, BigDecimal totalAmount, 
                                               BigDecimal installmentAmount, Competence initialCompetence, Account account) {
        BigDecimal amount = InstallmentCalculator.calculateCurrentInstallmentAmount(1, request.installments(), totalAmount, installmentAmount, BigDecimal.ZERO);
        Transaction root = createTransaction(request, userId, null, 1, amount, initialCompetence.getId());
        
        if (root.isPaid()) {
            transactionService.processTransaction(root, account);
        }
        return root;
    }

    private List<Transaction> generateChildren(CreateInstallmentRequest request, UUID userId, BigDecimal totalAmount, 
                                              BigDecimal installmentAmount, Competence initialCompetence, Account account, UUID parentId) {
        List<Transaction> children = new ArrayList<>();
        
        int currentMonth = initialCompetence.getMonth();
        int currentYear = initialCompetence.getYear();
        BigDecimal sumOfInstallments = InstallmentCalculator.calculateCurrentInstallmentAmount(1, request.installments(), totalAmount, installmentAmount, BigDecimal.ZERO);

        if (++currentMonth > 12) {
            currentMonth = 1;
            currentYear++;
        }

        for (int i = 2; i <= request.installments(); i++) {
            BigDecimal amount = InstallmentCalculator.calculateCurrentInstallmentAmount(i, request.installments(), totalAmount, installmentAmount, sumOfInstallments);
            sumOfInstallments = sumOfInstallments.add(amount);
            
            Competence competence = getOrCreateCompetence(userId, currentMonth, currentYear);
            Transaction transaction = createTransaction(request, userId, parentId, i, amount, competence.getId());
            
            children.add(transaction);
            if (transaction.isPaid()) {
                transactionService.processTransaction(transaction, account);
            }

            if (++currentMonth > 12) {
                currentMonth = 1;
                currentYear++;
            }
        }
        return children;
    }

    private BigDecimal resolveTotalAmount(CreateInstallmentRequest request) {
        if (Objects.nonNull(request.totalAmount())) {
            return request.totalAmount().setScale(2, RoundingMode.HALF_EVEN);
        }
        if (Objects.nonNull(request.installmentAmount())) {
            return InstallmentCalculator.calculateTotalAmount(request.installmentAmount(), request.installments());
        }
        throw new InstallmentValidationException("Either totalAmount or installmentAmount must be provided");
    }

    private BigDecimal resolveInstallmentAmount(CreateInstallmentRequest request, BigDecimal totalAmount) {
        if (Objects.nonNull(request.installmentAmount())) {
            return request.installmentAmount().setScale(2, RoundingMode.HALF_EVEN);
        }
        return InstallmentCalculator.calculateInstallmentAmount(totalAmount, request.installments());
    }


    private BigDecimal sumOfPrevious(List<Transaction> transactions) {
        return transactions.stream().map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Transaction createTransaction(CreateInstallmentRequest request, UUID userId, UUID parentId, int number, BigDecimal amount, UUID competenceId) {
        TransactionSubtype subtype = request.subtype() != null ? request.subtype() : TransactionSubtype.FIXED;
        return Transaction.create(
                request.accountId(), request.categoryId(), competenceId, userId,
                request.description(), amount, request.dateTime(), request.type(), subtype, request.status(),
                parentId, request.installments(), number, null
        );
    }

    private Account findAccount(UUID accountId, UUID userId) {
        return accountRepository.findByIdAndUserId(accountId, userId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));
    }

    private void validateCategory(UUID categoryId) {
        categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));
    }

    private Competence findCompetence(UUID competenceId) {
        return competenceRepository.findById(competenceId)
                .orElseThrow(() -> new CompetenceNotFoundException(competenceId));
    }

    public InstallmentSeries getInstallmentProgress(UUID parentId) {
        List<Transaction> transactions = findTransactionsByParent(parentId);
        return InstallmentSeries.fromTransactions(transactions);
    }

    @Transactional
    public InstallmentSeries updateInstallmentSeries(UUID parentId, UpdateInstallmentRequest request) {
        List<Transaction> transactions = findTransactionsByParent(parentId);

        List<Transaction> paidInstallments = transactions.stream().filter(Transaction::isPaid).toList();
        List<Transaction> pendingInstallments = transactions.stream()
                .filter(t -> !t.isPaid())
                .sorted(Comparator.comparingInt(Transaction::getInstallmentNumber))
                .toList();

        if (pendingInstallments.isEmpty()) {
            throw new InstallmentValidationException("Cannot update series where all installments are paid");
        }

        BigDecimal paidTotal = sumOfPrevious(paidInstallments);
        BigDecimal newTotal = request.newTotalAmount().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal newPendingTotal = newTotal.subtract(paidTotal);

        if (newPendingTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InstallmentValidationException("New total amount must be greater than the already paid amount (" + paidTotal + ")");
        }

        redistributeRemaining(pendingInstallments, newPendingTotal);

        return InstallmentSeries.fromTransactions(transactions);
    }

    private void redistributeRemaining(List<Transaction> pending, BigDecimal newPendingTotal) {
        BigDecimal installmentAmount = newPendingTotal.divide(BigDecimal.valueOf(pending.size()), 2, RoundingMode.HALF_EVEN);
        BigDecimal sumSoFar = BigDecimal.ZERO;

        for (int i = 0; i < pending.size(); i++) {
            Transaction t = pending.get(i);
            BigDecimal amount = InstallmentCalculator.calculateCurrentInstallmentAmount(i + 1, pending.size(), newPendingTotal, installmentAmount, sumSoFar);
            t.setAmount(amount);
            sumSoFar = sumSoFar.add(amount);
            transactionRepository.save(t);
        }
    }

    @Transactional
    public InstallmentSeries changeInstallmentType(UUID parentId, TransactionType newType) {
        List<Transaction> transactions = findTransactionsByParent(parentId);

        if (transactions.get(0).getType().equals(newType)) {
            return InstallmentSeries.fromTransactions(transactions);
        }

        Account account = findAccount(transactions.get(0).getAccountId(), transactions.get(0).getUserId());

        transactions.forEach(t -> {
            if (t.isPaid()) {
                transactionService.reverseTransaction(t, account);
                t.setType(newType);
                transactionService.processTransaction(t, account);
            } else {
                t.setType(newType);
            }
        });

        transactionRepository.saveAll(transactions);
        accountRepository.save(account);

        return InstallmentSeries.fromTransactions(transactions);
    }

    private List<Transaction> findTransactionsByParent(UUID parentId) {
        List<Transaction> transactions = transactionRepository.findByParentId(parentId);
        if (transactions.isEmpty()) {
            throw new TransactionNotFoundException(parentId);
        }
        return transactions;
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
    }
