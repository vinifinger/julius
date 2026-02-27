package com.finance.app.application.usecase;

import com.finance.app.domain.entity.CategoryExpenseSummary;
import com.finance.app.domain.entity.Competence;
import com.finance.app.domain.entity.CompetenceAmountSummary;
import com.finance.app.domain.entity.TransactionType;
import com.finance.app.domain.exception.CompetenceNotFoundException;
import com.finance.app.domain.repository.CompetenceRepository;
import com.finance.app.domain.repository.TransactionRepository;
import com.finance.app.web.dto.response.DashboardSummaryResponse;
import com.finance.app.web.dto.response.ExpenseByCategoryResponse;
import com.finance.app.web.dto.response.MonthlyEvolutionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardUseCase {

        private final TransactionRepository transactionRepository;
        private final CompetenceRepository competenceRepository;

        public DashboardSummaryResponse getSummary(UUID competenceId) {
                competenceRepository.findById(competenceId)
                                .orElseThrow(() -> new CompetenceNotFoundException(competenceId));

                BigDecimal totalRevenue = transactionRepository
                                .sumAmountByCompetenceIdAndType(competenceId, TransactionType.REVENUE)
                                .setScale(2, RoundingMode.HALF_EVEN);

                BigDecimal totalExpenses = transactionRepository
                                .sumAmountByCompetenceIdAndType(competenceId, TransactionType.EXPENSE)
                                .setScale(2, RoundingMode.HALF_EVEN);

                BigDecimal monthlyBalance = totalRevenue.subtract(totalExpenses)
                                .setScale(2, RoundingMode.HALF_EVEN);

                String status = determineStatus(monthlyBalance);

                return DashboardSummaryResponse.builder()
                                .totalRevenue(totalRevenue)
                                .totalExpenses(totalExpenses)
                                .monthlyBalance(monthlyBalance)
                                .status(status)
                                .build();
        }

        public List<ExpenseByCategoryResponse> getExpensesByCategory(UUID competenceId) {
                competenceRepository.findById(competenceId)
                                .orElseThrow(() -> new CompetenceNotFoundException(competenceId));

                List<CategoryExpenseSummary> categoryExpenseSummaries = transactionRepository
                                .sumExpensesByCategory(competenceId);

                BigDecimal totalExpenses = categoryExpenseSummaries.stream()
                                .map(CategoryExpenseSummary::totalAmount)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                List<ExpenseByCategoryResponse> responses = new ArrayList<>();
                for (CategoryExpenseSummary categoryExpenseSummary : categoryExpenseSummaries) {
                        BigDecimal amount = categoryExpenseSummary.totalAmount().setScale(2, RoundingMode.HALF_EVEN);

                        BigDecimal percentage = totalExpenses.compareTo(BigDecimal.ZERO) > 0
                                        ? amount.multiply(BigDecimal.valueOf(100)).divide(totalExpenses, 2,
                                                        RoundingMode.HALF_EVEN)
                                        : BigDecimal.ZERO.setScale(2, RoundingMode.HALF_EVEN);

                        responses.add(ExpenseByCategoryResponse.builder()
                                        .categoryName(categoryExpenseSummary.categoryName())
                                        .colorHex(categoryExpenseSummary.colorHex())
                                        .totalAmount(amount)
                                        .percentage(percentage)
                                        .build());
                }

                return responses;
        }

        public List<MonthlyEvolutionResponse> getEvolution(UUID userId) {
                List<Competence> competences = competenceRepository
                                .findByUserIdOrderByYearDescMonthDesc(userId);

                List<Competence> lastSix = competences.size() > 6
                                ? competences.subList(0, 6)
                                : competences;

                if (lastSix.isEmpty()) {
                        return List.of();
                }

                List<UUID> competenceIds = lastSix.stream()
                                .map(Competence::getId)
                                .toList();

                List<CompetenceAmountSummary> aggregatedData = transactionRepository
                                .sumAmountByCompetenceIds(competenceIds);

                Map<UUID, BigDecimal> revenueMap = new HashMap<>();
                Map<UUID, BigDecimal> expenseMap = new HashMap<>();

                for (CompetenceAmountSummary summary : aggregatedData) {
                        BigDecimal amount = summary.totalAmount().setScale(2, RoundingMode.HALF_EVEN);

                        if (TransactionType.REVENUE.equals(summary.type())) {
                                revenueMap.put(summary.competenceId(), amount);
                        } else {
                                expenseMap.put(summary.competenceId(), amount);
                        }
                }

                List<MonthlyEvolutionResponse> responses = new ArrayList<>();
                for (Competence competence : lastSix) {
                        BigDecimal revenue = revenueMap
                                        .getOrDefault(competence.getId(), BigDecimal.ZERO)
                                        .setScale(2, RoundingMode.HALF_EVEN);
                        BigDecimal expenses = expenseMap
                                        .getOrDefault(competence.getId(), BigDecimal.ZERO)
                                        .setScale(2, RoundingMode.HALF_EVEN);
                        BigDecimal balance = revenue.subtract(expenses)
                                        .setScale(2, RoundingMode.HALF_EVEN);

                        String competenceName = String.format("%02d/%d", competence.getMonth(), competence.getYear());

                        responses.add(MonthlyEvolutionResponse.builder()
                                        .month(competence.getMonth())
                                        .year(competence.getYear())
                                        .competenceName(competenceName)
                                        .totalRevenue(revenue)
                                        .totalExpenses(expenses)
                                        .balance(balance)
                                        .build());
                }

                return responses;
        }

        private String determineStatus(BigDecimal balance) {
                int comparison = balance.compareTo(BigDecimal.ZERO);
                if (comparison > 0) {
                        return "POSITIVE";
                } else if (comparison < 0) {
                        return "NEGATIVE";
                }
                return "NEUTRAL";
        }

}
