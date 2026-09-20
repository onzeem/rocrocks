package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Optional now — income transactions aren't tied to a spending
    // category. Only required (enforced in the controller, not here)
    // when type == EXPENSE.
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = true)
    private Category category;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal amount;

    private String description;

    @Column(nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type = TransactionType.EXPENSE;

    public Transaction() {
    }

    // Kept for backward compatibility with any existing call sites —
    // defaults to EXPENSE, matching the only behavior that existed
    // before income transactions were introduced.
    public Transaction(User user, Category category, BigDecimal amount, String description, LocalDate date) {
        this(user, category, amount, description, date, TransactionType.EXPENSE);
    }

    public Transaction(User user, Category category, BigDecimal amount, String description, LocalDate date, TransactionType type) {
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.description = description;
        this.date = date;
        this.type = type;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}