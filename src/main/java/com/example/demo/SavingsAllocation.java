package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * One row per "allocate funds" action — recorded so the monthly Savings
 * cap can be enforced cumulatively (sum of everything allocated this
 * month, across every goal) rather than only checking one request at a
 * time in isolation. SavingsGoal.savedAmount is a lifetime running
 * total; this table is what lets us ask "how much this month?"
 */
@Entity
@Table(name = "savings_allocations")
public class SavingsAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "savings_goal_id", nullable = false)
    private SavingsGoal savingsGoal;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDate date;

    public SavingsAllocation() {
    }

    public SavingsAllocation(User user, SavingsGoal savingsGoal, BigDecimal amount, LocalDate date) {
        this.user = user;
        this.savingsGoal = savingsGoal;
        this.amount = amount;
        this.date = date;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public SavingsGoal getSavingsGoal() { return savingsGoal; }
    public void setSavingsGoal(SavingsGoal savingsGoal) { this.savingsGoal = savingsGoal; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}