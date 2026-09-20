package com.example.demo;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CategoryReportItem {
    private String categoryName;
    private String icon;
    private String color;
    private String description;
    private BigDecimal budgeted;
    private BigDecimal actual;
    private BigDecimal difference;
    private BigDecimal percentOfIncomeSpent;
    private boolean overBudget;

    public CategoryReportItem(String categoryName, String icon, String color, String description,
                               BigDecimal budgeted, BigDecimal actual, BigDecimal totalIncome) {
        this.categoryName = categoryName;
        this.icon = icon;
        this.color = color;
        this.description = description;
        this.budgeted = budgeted;
        this.actual = actual;
        this.difference = budgeted.subtract(actual);
        this.overBudget = this.difference.compareTo(BigDecimal.ZERO) < 0;

        if (totalIncome != null && totalIncome.compareTo(BigDecimal.ZERO) > 0) {
            this.percentOfIncomeSpent = actual
                    .divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            this.percentOfIncomeSpent = BigDecimal.ZERO;
        }
    }

    public String getCategoryName() { return categoryName; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
    public String getDescription() { return description; }
    public BigDecimal getBudgeted() { return budgeted; }
    public BigDecimal getActual() { return actual; }
    public BigDecimal getDifference() { return difference; }
    public BigDecimal getPercentOfIncomeSpent() { return percentOfIncomeSpent; }
    public boolean isOverBudget() { return overBudget; }
}