package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public ReportController(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    @GetMapping("/budget-vs-actual/user/{userId}/{year}/{month}")
    public ResponseEntity<BudgetVsActualReport> getBudgetVsActual(
            @PathVariable Long userId, @PathVariable int year, @PathVariable int month) {

        Budget budget = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .orElseThrow(() -> new RuntimeException(
                        "No budget found for user " + userId + " in " + month + "/" + year));

        Map<String, BigDecimal> budgetedByCategory = new LinkedHashMap<>();
        for (BudgetCategory bc : budget.getBudgetCategories()) {
            budgetedByCategory.put(bc.getCategory().getName(), bc.getAllocatedAmount());
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        Map<String, BigDecimal> actualByCategory = new LinkedHashMap<>();
        for (Transaction t : transactionRepository.findByUserIdAndDateBetween(userId, start, end)) {
            String categoryName = t.getCategory().getName();
            actualByCategory.merge(categoryName, t.getAmount(), BigDecimal::add);
        }

        Map<String, CategoryReportItem> merged = new LinkedHashMap<>();
        for (String categoryName : budgetedByCategory.keySet()) {
            BigDecimal budgeted = budgetedByCategory.get(categoryName);
            BigDecimal actual = actualByCategory.getOrDefault(categoryName, BigDecimal.ZERO);
            merged.put(categoryName, new CategoryReportItem(categoryName, budgeted, actual));
        }
        for (String categoryName : actualByCategory.keySet()) {
            if (!merged.containsKey(categoryName)) {
                merged.put(categoryName, new CategoryReportItem(categoryName, BigDecimal.ZERO,
                        actualByCategory.get(categoryName)));
            }
        }

        BigDecimal totalBudgeted = budgetedByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalActual = actualByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BudgetVsActualReport report = new BudgetVsActualReport(
                month, year, totalBudgeted, totalActual, merged.values().stream().toList());

        return ResponseEntity.ok(report);
    }
}
