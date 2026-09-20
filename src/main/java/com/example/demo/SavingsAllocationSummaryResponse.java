package com.example.demo;

import java.math.BigDecimal;

public class SavingsAllocationSummaryResponse {
    private BigDecimal availableToSave; // real income minus real expenses this month, floored at 0
    private BigDecimal totalSaved;      // sum of savedAmount across every goal — includes preloaded starting amounts
    private BigDecimal remaining;       // availableToSave - totalSaved, floored at 0

    public SavingsAllocationSummaryResponse(BigDecimal availableToSave, BigDecimal totalSaved, BigDecimal remaining) {
        this.availableToSave = availableToSave;
        this.totalSaved = totalSaved;
        this.remaining = remaining;
    }

    public BigDecimal getAvailableToSave() { return availableToSave; }
    public BigDecimal getTotalSaved() { return totalSaved; }
    public BigDecimal getRemaining() { return remaining; }
}