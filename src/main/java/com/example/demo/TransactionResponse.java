package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionResponse {
    private Long id;
    private String categoryName;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

    public TransactionResponse(Long id, String categoryName, BigDecimal amount, String description, LocalDate date) {
        this.id = id;
        this.categoryName = categoryName;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public Long getId() { return id; }
    public String getCategoryName() { return categoryName; }
    public BigDecimal getAmount() { return amount; }
    public String getDescription() { return description; }
    public LocalDate getDate() { return date; }
}
