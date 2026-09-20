package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateSavingsGoalRequest {
    private Long userId;
    private String name;
    private String icon;
    private String color;
    private BigDecimal targetAmount;
    private BigDecimal savedAmount; // optional — defaults to 0 if omitted
    private LocalDate dueDate;      // optional
    private String note;            // optional

    public CreateSavingsGoalRequest() {}

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

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