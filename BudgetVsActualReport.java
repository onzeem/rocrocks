package com.example.demo;

import java.math.BigDecimal;
import java.util.List;

public class BudgetVsActualReport {
    private int month;
    private int year;
    private BigDecimal totalBudgeted;
    private BigDecimal totalActual;
    private BigDecimal totalDifference;
    private List<CategoryReportItem> categories;

    public BudgetVsActualReport(int month, int year, BigDecimal totalBudgeted,
                                 BigDecimal totalActual, List<CategoryReportItem> categories) {
        this.month = month;
        this.year = year;
        this.totalBudgeted = totalBudgeted;
        this.totalActual = totalActual;
        this.totalDifference = totalBudgeted.subtract(totalActual);
        this.categories = categories;
    }

    public int getMonth() { return month; }
    public int getYear() { return year; }
    public BigDecimal getTotalBudgeted() { return totalBudgeted; }
    public BigDecimal getTotalActual() { return totalActual; }
    public BigDecimal getTotalDifference() { return totalDifference; }
    public List<CategoryReportItem> getCategories() { return categories; }
}
