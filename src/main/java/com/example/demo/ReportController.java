package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.List;
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
        Map<String, Category> categoryByName = new LinkedHashMap<>();
        for (BudgetCategory bc : budget.getBudgetCategories()) {
            budgetedByCategory.put(bc.getCategory().getName(), bc.getAllocatedAmount());
            categoryByName.put(bc.getCategory().getName(), bc.getCategory());
        }

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // Actual income for the month — summed directly from INCOME-type
        // transactions, rather than the budget's own totalIncome field.
        // That field is often just $0 now (e.g. budgets auto-created via
        // addOrUpdateCategory never set it), so this reflects real
        // tracked income instead of a number that may never get set.
        List<Transaction> monthTransactions = transactionRepository.findByUserIdAndDateBetween(userId, start, end);

        BigDecimal actualIncome = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Income transactions don't belong to a category (getCategory()
        // is null for them) and shouldn't count as "spent" anywhere, so
        // they're skipped entirely when building actualByCategory.
        Map<String, BigDecimal> actualByCategory = new LinkedHashMap<>();
        for (Transaction t : monthTransactions) {
            if (t.getType() == TransactionType.INCOME || t.getCategory() == null) {
                continue;
            }
            String categoryName = t.getCategory().getName();
            actualByCategory.merge(categoryName, t.getAmount(), BigDecimal::add);
            // A category might have actual spending this month without
            // being on the budget at all — still need its icon/color,
            // so record it here too if budgetedByCategory didn't already.
            categoryByName.putIfAbsent(categoryName, t.getCategory());
        }

        Map<String, CategoryReportItem> merged = new LinkedHashMap<>();
        for (String categoryName : budgetedByCategory.keySet()) {
            BigDecimal budgeted = budgetedByCategory.get(categoryName);
            BigDecimal actual = actualByCategory.getOrDefault(categoryName, BigDecimal.ZERO);
            Category category = categoryByName.get(categoryName);
            merged.put(categoryName, new CategoryReportItem(
                    categoryName, category.getIcon(), category.getColor(), category.getDescription(),
                    budgeted, actual, actualIncome));
        }
        for (String categoryName : actualByCategory.keySet()) {
            if (!merged.containsKey(categoryName)) {
                Category category = categoryByName.get(categoryName);
                merged.put(categoryName, new CategoryReportItem(
                        categoryName, category.getIcon(), category.getColor(), category.getDescription(),
                        BigDecimal.ZERO, actualByCategory.get(categoryName), actualIncome));
            }
        }

        BigDecimal totalBudgeted = budgetedByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalActual = actualByCategory.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BudgetVsActualReport report = new BudgetVsActualReport(
                month, year, totalBudgeted, totalActual, actualIncome,
                merged.values().stream().toList());

        return ResponseEntity.ok(report);
    }
}