package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SavingsGoalResponse {
    private Long id;
    private String name;
    private String icon;
    private String color;
    private BigDecimal savedAmount;
    private BigDecimal targetAmount;
    private LocalDate dueDate;
    private String note;
    private int percentFunded;
    private boolean fullyFunded;

    public SavingsGoalResponse(Long id, String name, String icon, String color,
                                BigDecimal savedAmount, BigDecimal targetAmount,
                                LocalDate dueDate, String note,
                                int percentFunded, boolean fullyFunded) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.color = color;
        this.savedAmount = savedAmount;
        this.targetAmount = targetAmount;
        this.dueDate = dueDate;
        this.note = note;
        this.percentFunded = percentFunded;
        this.fullyFunded = fullyFunded;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getIcon() { return icon; }
    public String getColor() { return color; }
    public BigDecimal getSavedAmount() { return savedAmount; }
    public BigDecimal getTargetAmount() { return targetAmount; }
    public LocalDate getDueDate() { return dueDate; }
    public String getNote() { return note; }
    public int getPercentFunded() { return percentFunded; }
    public boolean isFullyFunded() { return fullyFunded; }
}