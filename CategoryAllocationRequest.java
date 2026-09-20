package com.example.demo;
import java.math.BigDecimal;

public class CategoryAllocationRequest {
    private Long categoryId;
    private BigDecimal amount;

    public CategoryAllocationRequest() {}

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}