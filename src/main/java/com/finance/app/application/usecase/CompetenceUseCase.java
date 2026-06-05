package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.CompetenceTransactionCountSummary;
import com.finance.app.domain.entity.CompetenceTransactionAmountSummary;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.web.dto.request.CreateCompetenceRequest;
import com.finance.app.web.dto.response.CompetenceResponse;
import com.finance.app.web.dto.response.CompetenceDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import com.finance.app.domain.exception.CompetenceNotFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompetenceUseCase {

    private final CompetenceRepository competenceRepository;
    private final TransactionRepository transactionRepository;

    public CompetenceDetailResponse create(CreateCompetenceRequest request, UUID userId) {
        Competence competence = getOrCreate(request.month(), request.year(), userId);
        return getCompetenceDetailWithAmounts(competence);
    }

    public Competence getOrCreate(Integer month, Integer year, UUID userId) {
        Optional<Competence> existing = competenceRepository.findByUserIdAndMonthAndYear(
                userId, month, year);

        if (existing.isPresent()) {
            return existing.get();
        }

        LocalDateTime now = LocalDateTime.now();
        Competence competence = Competence.builder()
                .userId(userId)
                .month(month)
                .year(year)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Competence savedCompetence = competenceRepository.save(competence);
        log.atInfo().log("Created new competence ID {} for month/year: {}/{} for user ID {}", 
                savedCompetence.getId(), month, year, userId);
        return savedCompetence;
    }

    public CompetenceDetailResponse getById(UUID id, UUID userId) {
        Competence competence = competenceRepository.findById(id)
                .filter(c -> c.getUserId().equals(userId))
                .orElseThrow(() -> new CompetenceNotFoundException(id));
        return getCompetenceDetailWithAmounts(competence);
    }

    public List<CompetenceResponse> listAll(UUID userId) {
        List<Competence> competences = competenceRepository.findByUserIdOrderByYearDescMonthDesc(userId);
        List<CompetenceTransactionCountSummary> counts = transactionRepository.countTransactionsGroupedByCompetence(userId);
        Map<UUID, Long> countMap = counts.stream()
                .collect(Collectors.toMap(
                        CompetenceTransactionCountSummary::competenceId,
                        CompetenceTransactionCountSummary::transactionCount
                ));

        List<CompetenceTransactionAmountSummary> amountSummaries = transactionRepository.sumAmountsGroupedByCompetence(userId);
        Map<UUID, List<CompetenceTransactionAmountSummary>> amountsMap = amountSummaries.stream()
                .collect(Collectors.groupingBy(CompetenceTransactionAmountSummary::competenceId));

        return competences.stream()
                .map(comp -> {
                    long transactionCount = countMap.getOrDefault(comp.getId(), 0L);
                    List<CompetenceTransactionAmountSummary> summaries = amountsMap.getOrDefault(comp.getId(), List.of());

                    BigDecimal totalRevenue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);
                    BigDecimal totalExpense = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);

                    for (CompetenceTransactionAmountSummary summary : summaries) {
                        BigDecimal amount = java.util.Objects.nonNull(summary.totalAmount())
                                ? summary.totalAmount().setScale(2, RoundingMode.HALF_EVEN)
                                : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);

                        if (summary.type() == TransactionType.REVENUE) {
                            totalRevenue = totalRevenue.add(amount);
                        } else {
                            totalExpense = totalExpense.add(amount);
                        }
                    }

                    return CompetenceResponse.fromDomain(comp, transactionCount, totalRevenue, totalExpense);
                })
                .toList();
    }

    public CompetenceDetailResponse getCurrent(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        Integer currentMonth = now.getMonthValue();
        Integer currentYear = now.getYear();

        Optional<Competence> existing = competenceRepository.findByUserIdAndMonthAndYear(
                userId, currentMonth, currentYear);

        if (existing.isPresent()) {
            Competence comp = existing.get();
            return getCompetenceDetailWithAmounts(comp);
        }

        Competence competence = Competence.builder()
                .userId(userId)
                .month(currentMonth)
                .year(currentYear)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Competence savedCompetence = competenceRepository.save(competence);
        log.atInfo().log("Auto-created current competence ID {} for month/year: {}/{} for user ID {}", 
                savedCompetence.getId(), currentMonth, currentYear, userId);
        return getCompetenceDetailWithAmounts(savedCompetence);
    }

    private CompetenceDetailResponse getCompetenceDetailWithAmounts(Competence competence) {
        long transactionCount = transactionRepository.countByCompetenceId(competence.getId());
        List<CompetenceTransactionAmountSummary> summaries = transactionRepository.sumAmountsByCompetenceId(competence.getId());

        BigDecimal paidAmount = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (CompetenceTransactionAmountSummary summary : summaries) {
            BigDecimal amount = java.util.Objects.nonNull(summary.totalAmount())
                    ? summary.totalAmount()
                    : BigDecimal.ZERO;

            if (summary.type() == TransactionType.REVENUE) {
                totalRevenue = totalRevenue.add(amount);
                if (summary.status() == TransactionStatus.PAID) {
                    paidAmount = paidAmount.add(amount);
                } else {
                    pendingAmount = pendingAmount.add(amount);
                }
            } else {
                totalExpense = totalExpense.add(amount);
                if (summary.status() == TransactionStatus.PAID) {
                    paidAmount = paidAmount.subtract(amount);
                } else {
                    pendingAmount = pendingAmount.subtract(amount);
                }
            }
        }

        BigDecimal totalAmount = paidAmount.add(pendingAmount);

        return CompetenceDetailResponse.fromDomain(competence, transactionCount, paidAmount, pendingAmount, totalAmount, totalRevenue, totalExpense);
    }

}
