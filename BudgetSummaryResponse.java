package com.example.demo;
import java.math.BigDecimal;
import java.util.List;

public class BudgetSummaryResponse {
    private Long id;
    private BigDecimal totalIncome;
    private BigDecimal totalAllocated;
    private BigDecimal remainingIncome;
    private boolean overAllocated;
    private List<CategorySummaryItem> items;

    public BudgetSummaryResponse(Long id, BigDecimal totalIncome, BigDecimal totalAllocated,
                                 BigDecimal remainingIncome, boolean overAllocated,
                                 List<CategorySummaryItem> items) {
        this.id = id;
        this.totalIncome = totalIncome;
        this.totalAllocated = totalAllocated;
        this.remainingIncome = remainingIncome;
        this.overAllocated = overAllocated;
        this.items = items;
    }

    public Long getId() { return id; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public BigDecimal getTotalAllocated() { return totalAllocated; }
    public BigDecimal getRemainingIncome() { return remainingIncome; }
    public boolean isOverAllocated() { return overAllocated; }
    public List<CategorySummaryItem> getItems() { return items; }
}
