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

@Entity
@Table(name = "savings_goals")
public class SavingsGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    // Emoji shown on the goal card, e.g. "🛟", "✈️" — purely cosmetic,
    // matching how categories/transactions already carry their own icon.
    @Column(nullable = false)
    private String icon;

    // One of the site's theme colors: grape, coral, mint, yellow, pink, sky.
    @Column(nullable = false)
    private String color;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal targetAmount;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal savedAmount = BigDecimal.ZERO;

    // Optional — not every goal has a deadline.
    private LocalDate dueDate;

    // Optional short status line, e.g. "Add $130/mo to make the date".
    private String note;

    public SavingsGoal() {
    }

    public SavingsGoal(User user, String name, String icon, String color,
                        BigDecimal targetAmount, BigDecimal savedAmount,
                        LocalDate dueDate, String note) {
        this.user = user;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.targetAmount = targetAmount;
        this.savedAmount = savedAmount != null ? savedAmount : BigDecimal.ZERO;
        this.dueDate = dueDate;
        this.note = note;
    }

    // Adds funds toward this goal — used by the "allocate funds" endpoint.
    public void addFunds(BigDecimal amount) {
        this.savedAmount = this.savedAmount.add(amount);
    }

    public int getPercentFunded() {
        if (targetAmount == null || targetAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }
        return savedAmount
                .divide(targetAmount, 4, java.math.RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .intValue();
    }

    public boolean isFullyFunded() {
        return savedAmount.compareTo(targetAmount) >= 0;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public BigDecimal getTargetAmount() { return targetAmount; }
    public void setTargetAmount(BigDecimal targetAmount) { this.targetAmount = targetAmount; }

    public BigDecimal getSavedAmount() { return savedAmount; }
    public void setSavedAmount(BigDecimal savedAmount) { this.savedAmount = savedAmount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}