package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {
    private Long id;
    private String categoryName;
    private BigDecimal amount;
    private LocalDate date;
    private String description;

    public TransactionResponse(Long id, String categoryName, BigDecimal amount, LocalDate date, String description) {
        this.id = id;
        this.categoryName = categoryName;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}