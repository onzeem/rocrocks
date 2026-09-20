package com.example.demo;

import java.math.BigDecimal;

public class AllocateFundsRequest {
    private BigDecimal amount;

    public AllocateFundsRequest() {}

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}