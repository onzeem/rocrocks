package com.example.demo;


import java.math.BigDecimal;

public class CategorySummaryItem {
    private String categoryName;
    private BigDecimal allocatedAmount;

    public CategorySummaryItem(String categoryName, BigDecimal allocatedAmount) {
        this.categoryName = categoryName;
        this.allocatedAmount = allocatedAmount;
    }

    public String getCategoryName() { return categoryName; }
    public BigDecimal getAllocatedAmount() { return allocatedAmount; }
}