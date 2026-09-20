package com.example.demo;

import java.math.BigDecimal;

public class CategoryReportItem {
    private String categoryName;
    private BigDecimal budgeted;
    private BigDecimal actual;
    private BigDecimal difference;

    public CategoryReportItem(String categoryName, BigDecimal budgeted, BigDecimal actual) {
        this.categoryName = categoryName;
        this.budgeted = budgeted;
        this.actual = actual;
        this.difference = budgeted.subtract(actual);
    }

    public String getCategoryName() { return categoryName; }
    public BigDecimal getBudgeted() { return budgeted; }
    public BigDecimal getActual() { return actual; }
    public BigDecimal getDifference() { return difference; }
}
