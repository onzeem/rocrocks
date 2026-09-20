package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTransactionRequest {
    private Long userId;
    private Long categoryId;
    private BigDecimal amount;
    private String description;
    private LocalDate date;

    public CreateTransactionRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}
