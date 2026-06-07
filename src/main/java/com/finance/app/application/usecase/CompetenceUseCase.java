package com.finance.app.application.usecase;

import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.CompetenceTransactionCountSummary;
import com.finance.app.domain.entity.CompetenceTransactionAmountSummary;
import com.finance.app.domain.entity.CompetenceTransactionSubtypeSummary;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.entity.TransactionStatus;
import com.finance.app.domain.entity.TransactionSubtype;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.web.dto.request.CreateCompetenceRequest;
import com.finance.app.web.dto.response.CompetenceResponse;
import com.finance.app.web.dto.response.CompetenceDetailResponse;
import com.finance.app.web.dto.response.CompetenceDetailResponseV2;
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

    public CompetenceDetailResponseV2 getByIdV2(UUID id, UUID userId) {
        Competence competence = competenceRepository.findById(id)
                .filter(c -> c.getUserId().equals(userId))
                .orElseThrow(() -> new CompetenceNotFoundException(id));
        return getCompetenceDetailV2(competence);
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

        BigDecimal completedAmount = BigDecimal.ZERO;
        BigDecimal pendingAmount = BigDecimal.ZERO;
        BigDecimal totalRevenue = BigDecimal.ZERO;
        BigDecimal totalExpense = BigDecimal.ZERO;

        for (CompetenceTransactionAmountSummary summary : summaries) {
            BigDecimal amount = java.util.Objects.nonNull(summary.totalAmount())
                    ? summary.totalAmount()
                    : BigDecimal.ZERO;

            if (summary.type() == TransactionType.REVENUE) {
                totalRevenue = totalRevenue.add(amount);
                if (summary.status() == TransactionStatus.COMPLETED) {
                    completedAmount = completedAmount.add(amount);
                } else {
                    pendingAmount = pendingAmount.add(amount);
                }
            } else {
                totalExpense = totalExpense.add(amount);
                if (summary.status() == TransactionStatus.COMPLETED) {
                    completedAmount = completedAmount.subtract(amount);
                } else {
                    pendingAmount = pendingAmount.subtract(amount);
                }
            }
        }

        BigDecimal totalAmount = completedAmount.add(pendingAmount);

        return CompetenceDetailResponse.fromDomain(competence, transactionCount, completedAmount, pendingAmount, totalAmount, totalRevenue, totalExpense);
    }

    private CompetenceDetailResponseV2 getCompetenceDetailV2(Competence competence) {
        List<CompetenceTransactionSubtypeSummary> summaries = transactionRepository.sumSubtypeAmountsByCompetenceId(competence.getId());

        // We will accumulate these directly
        long pendingCount = 0;
        BigDecimal pendingFixedRev = BigDecimal.ZERO;
        BigDecimal pendingFixedExp = BigDecimal.ZERO;
        BigDecimal pendingVarRev = BigDecimal.ZERO;
        BigDecimal pendingVarExp = BigDecimal.ZERO;

        long completedCount = 0;
        BigDecimal completedFixedRev = BigDecimal.ZERO;
        BigDecimal completedFixedExp = BigDecimal.ZERO;
        BigDecimal completedVarRev = BigDecimal.ZERO;
        BigDecimal completedVarExp = BigDecimal.ZERO;

        for (CompetenceTransactionSubtypeSummary summary : summaries) {
            BigDecimal amount = java.util.Objects.nonNull(summary.totalAmount()) ? summary.totalAmount() : BigDecimal.ZERO;
            long count = java.util.Objects.nonNull(summary.transactionCount()) ? summary.transactionCount() : 0L;

            boolean isCompleted = TransactionStatus.COMPLETED.equals(summary.status());
            boolean isFixed = TransactionSubtype.FIXED.equals(summary.subtype());
            boolean isRev = TransactionType.REVENUE.equals(summary.type());

            if (isCompleted) {
                completedCount += count;
                if (isFixed) {
                    if (isRev) completedFixedRev = completedFixedRev.add(amount);
                    else completedFixedExp = completedFixedExp.add(amount);
                } else {
                    if (isRev) completedVarRev = completedVarRev.add(amount);
                    else completedVarExp = completedVarExp.add(amount);
                }
            } else {
                pendingCount += count;
                if (isFixed) {
                    if (isRev) pendingFixedRev = pendingFixedRev.add(amount);
                    else pendingFixedExp = pendingFixedExp.add(amount);
                } else {
                    if (isRev) pendingVarRev = pendingVarRev.add(amount);
                    else pendingVarExp = pendingVarExp.add(amount);
                }
            }
        }

        CompetenceDetailResponseV2.SummaryDetail pendingSummary = buildSummaryDetail(pendingCount, pendingFixedRev, pendingFixedExp, pendingVarRev, pendingVarExp);
        CompetenceDetailResponseV2.SummaryDetail completedSummary = buildSummaryDetail(completedCount, completedFixedRev, completedFixedExp, completedVarRev, completedVarExp);

        BigDecimal totalRev = pendingSummary.totalSummary().totalRevenue().add(completedSummary.totalSummary().totalRevenue());
        BigDecimal totalExp = pendingSummary.totalSummary().totalExpense().add(completedSummary.totalSummary().totalExpense());
        BigDecimal totalBal = pendingSummary.totalSummary().totalBalance().add(completedSummary.totalSummary().totalBalance());

        CompetenceDetailResponseV2.TotalSummaryDetail totalSummary = CompetenceDetailResponseV2.TotalSummaryDetail.builder()
                .totalRevenue(totalRev)
                .totalExpense(totalExp)
                .totalBalance(totalBal)
                .build();

        String name = String.format("%02d/%d", competence.getMonth(), competence.getYear());

        return CompetenceDetailResponseV2.builder()
                .id(competence.getId())
                .name(name)
                .month(competence.getMonth())
                .year(competence.getYear())
                .pendingSummary(pendingSummary)
                .completedSummary(completedSummary)
                .totalSummary(totalSummary)
                .createdAt(competence.getCreatedAt())
                .updatedAt(competence.getUpdatedAt())
                .build();
    }

    private CompetenceDetailResponseV2.SummaryDetail buildSummaryDetail(long count, BigDecimal fixedRev, BigDecimal fixedExp, BigDecimal varRev, BigDecimal varExp) {
        // According to the JSON contract, expense is returned as negative
        BigDecimal fixedExpNeg = fixedExp.negate().setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal varExpNeg = varExp.negate().setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal scaledFixedRev = fixedRev.setScale(2, RoundingMode.HALF_EVEN);
        BigDecimal scaledVarRev = varRev.setScale(2, RoundingMode.HALF_EVEN);

        BigDecimal fixedBal = scaledFixedRev.add(fixedExpNeg);
        BigDecimal varBal = scaledVarRev.add(varExpNeg);

        CompetenceDetailResponseV2.AmountDetail fixed = CompetenceDetailResponseV2.AmountDetail.builder()
                .revenue(scaledFixedRev)
                .expense(fixedExpNeg)
                .balance(fixedBal)
                .build();

        CompetenceDetailResponseV2.AmountDetail variable = CompetenceDetailResponseV2.AmountDetail.builder()
                .revenue(scaledVarRev)
                .expense(varExpNeg)
                .balance(varBal)
                .build();

        BigDecimal totalRev = scaledFixedRev.add(scaledVarRev);
        BigDecimal totalExp = fixedExpNeg.add(varExpNeg);
        BigDecimal totalBal = fixedBal.add(varBal);

        CompetenceDetailResponseV2.TotalSummaryDetail totalSummary = CompetenceDetailResponseV2.TotalSummaryDetail.builder()
                .totalRevenue(totalRev)
                .totalExpense(totalExp)
                .totalBalance(totalBal)
                .build();

        return CompetenceDetailResponseV2.SummaryDetail.builder()
                .transactionCount(count)
                .fixed(fixed)
                .variable(variable)
                .totalSummary(totalSummary)
                .build();
    }

}
