package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class BudgetVsActualReport {
    private int month;
    private int year;
    private BigDecimal totalIncome;
    private BigDecimal totalBudgeted;
    private BigDecimal totalActual;
    private BigDecimal totalDifference;
    private BigDecimal totalPercentOfIncomeSpent;
    private List<CategoryReportItem> categories;

    public BudgetVsActualReport(int month, int year, BigDecimal totalBudgeted,
                                 BigDecimal totalActual, BigDecimal totalIncome,
                                 List<CategoryReportItem> categories) {
        this.month = month;
        this.year = year;
        this.totalIncome = totalIncome;
        this.totalBudgeted = totalBudgeted;
        this.totalActual = totalActual;
        this.totalDifference = totalBudgeted.subtract(totalActual);
        this.categories = categories;

        if (totalIncome != null && totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            this.totalPercentOfIncomeSpent = totalActual
                    .divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            this.totalPercentOfIncomeSpent = BigDecimal.ZERO;
        }
    }

    public int getMonth() { return month; }
    public int getYear() { return year; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalBudgeted() { return totalBudgeted; }
    public BigDecimal getTotalActual() { return totalActual; }
    public BigDecimal getTotalDifference() { return totalDifference; }
    public BigDecimal getTotalPercentOfIncomeSpent() { return totalPercentOfIncomeSpent; }
    public List<CategoryReportItem> getCategories() { return categories; }
}
