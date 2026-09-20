package com.example.demo;
import java.math.BigDecimal;
import java.util.List;

public class CreateBudgetRequest {
    private Long userId;
    private int month;
    private int year;
    private BigDecimal totalIncome;
    private List<CategoryAllocationRequest> categories;

    public CreateBudgetRequest() {}

    public CreateBudgetRequest(List<CategoryAllocationRequest> categories, int month, BigDecimal totalIncome, Long userId, int year) {
        this.categories = categories;
        this.month = month;
        this.totalIncome = totalIncome;
        this.userId = userId;
        this.year = year;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }

    public List<CategoryAllocationRequest> getCategories() { return categories; }
    public void setCategories(List<CategoryAllocationRequest> categories) { this.categories = categories; }
}