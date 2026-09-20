package com.example.demo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "budget_month")
    private int month;
    @Column(name = "budget_year")
    private int year;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalIncome;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BudgetCategory> budgetCategories = new ArrayList<>();

    // Required by JPA
    public Budget() {
    }

    public Budget(User user, int month, int year, BigDecimal totalIncome) {
        this.user = user;
        this.month = month;
        this.year = year;
        this.totalIncome = totalIncome;
    }

    // Bidirectional helper methods
    public void addBudgetCategory(BudgetCategory budgetCategory) {
        budgetCategories.add(budgetCategory);
        budgetCategory.setBudget(this);
    }

    public void removeBudgetCategory(BudgetCategory budgetCategory) {
        budgetCategories.remove(budgetCategory);
        budgetCategory.setBudget(null);
    }

    // Calculated fields
    public BigDecimal getTotalAllocated() {
        if (budgetCategories == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (BudgetCategory bc : budgetCategories) {
            if (bc.getAllocatedAmount() != null) {
                total = total.add(bc.getAllocatedAmount());
            }
        }
        return total;
    }

    public BigDecimal getRemainingIncome() {
        if (totalIncome == null) {
            return BigDecimal.ZERO;
        }
        return totalIncome.subtract(getTotalAllocated());
    }

    public boolean isOverAllocated() {
        if (totalIncome == null) {
            return false;
        }
        return getTotalAllocated().compareTo(totalIncome) > 0;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public List<BudgetCategory> getBudgetCategories() {
        return budgetCategories;
    }

    public void setBudgetCategories(List<BudgetCategory> budgetCategories) {
        this.budgetCategories = budgetCategories;
    }
}