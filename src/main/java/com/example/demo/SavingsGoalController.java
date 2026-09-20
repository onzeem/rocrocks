package com.example.demo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/savings-goals")
public class SavingsGoalController {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public SavingsGoalController(SavingsGoalRepository savingsGoalRepository,
                                  UserRepository userRepository,
                                  TransactionRepository transactionRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }

    @PostMapping
    public ResponseEntity<SavingsGoalResponse> createGoal(@RequestBody CreateSavingsGoalRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        if (request.getTargetAmount() == null || request.getTargetAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("targetAmount must be greater than 0");
        }

        SavingsGoal goal = new SavingsGoal(
                user,
                request.getName(),
                request.getIcon(),
                request.getColor(),
                request.getTargetAmount(),
                request.getSavedAmount(), // SavingsGoal's constructor defaults null -> ZERO
                request.getDueDate(),
                request.getNote()
        );

        SavingsGoal saved = savingsGoalRepository.save(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<SavingsGoalResponse>> getByUser(@PathVariable Long userId) {
        List<SavingsGoalResponse> results = savingsGoalRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }

    /**
     * How much is genuinely available to save this month, how much is
     * already sitting in savings goals (savedAmount already includes
     * everything — whatever was preloaded plus anything allocated
     * later, since addFunds() just adds onto whatever's already there),
     * and how much is left over after that. Used by savings.html to
     * show a summary above the goal cards.
     */
    @GetMapping("/user/{userId}/allocation-summary")
    public ResponseEntity<SavingsAllocationSummaryResponse> getAllocationSummary(@PathVariable Long userId) {
        BigDecimal availableToSave = getAvailableToSave(userId);
        BigDecimal totalSaved = getTotalSaved(userId);
        BigDecimal remaining = availableToSave.subtract(totalSaved).max(BigDecimal.ZERO);

        return ResponseEntity.ok(new SavingsAllocationSummaryResponse(availableToSave, totalSaved, remaining));
    }

    /**
     * Adds money toward an existing goal — e.g. "I just put $50 toward
     * my Iceland trip." Increments savedAmount in place rather than
     * requiring the whole goal to be recreated or replaced.
     *
     * Capped against real cash flow this month — "available to save" is
     * actual INCOME-type transaction totals minus actual EXPENSE-type
     * transaction totals, not a fixed preset number. Whatever's already
     * saved across every one of the user's goals (savedAmount already
     * includes preloaded starting amounts, not just prior allocations)
     * is subtracted from that, so a request is only approved if it fits
     * within what's genuinely left over.
     */
    @PostMapping("/{id}/allocate")
    public ResponseEntity<SavingsGoalResponse> allocateFunds(
            @PathVariable Long id, @RequestBody AllocateFundsRequest request) {

        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found with ID: " + id));

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("amount must be greater than 0");
        }

        Long userId = goal.getUser().getId();

        BigDecimal availableToSave = getAvailableToSave(userId);
        BigDecimal totalSaved = getTotalSaved(userId);
        BigDecimal remaining = availableToSave.subtract(totalSaved).max(BigDecimal.ZERO);

        if (request.getAmount().compareTo(remaining) > 0) {
            throw new RuntimeException("Only $" + remaining + " left available to save");
        }

        goal.addFunds(request.getAmount());
        SavingsGoal savedGoal = savingsGoalRepository.save(goal);

        return ResponseEntity.ok(toResponse(savedGoal));
    }

    // Real income minus real expenses this month, floored at 0 — summed
    // straight from actual Transaction rows, the same way ReportController
    // computes them for the budget-vs-actual report.
    private BigDecimal getAvailableToSave(Long userId) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate monthStart = currentMonth.atDay(1);
        LocalDate monthEnd = currentMonth.atEndOfMonth();

        List<Transaction> monthTransactions = transactionRepository.findByUserIdAndDateBetween(userId, monthStart, monthEnd);

        BigDecimal income = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal expenses = monthTransactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return income.subtract(expenses).max(BigDecimal.ZERO);
    }

    // Sum of savedAmount across every one of the user's goals — this
    // already includes whatever was preloaded (e.g. from data.sql) as
    // well as anything added later through allocateFunds, since
    // SavingsGoal.addFunds() just adds onto whatever's already there.
    private BigDecimal getTotalSaved(Long userId) {
        return savingsGoalRepository.findByUserId(userId).stream()
                .map(SavingsGoal::getSavedAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        if (!savingsGoalRepository.existsById(id)) {
            throw new RuntimeException("Savings goal not found with ID: " + id);
        }
        savingsGoalRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private SavingsGoalResponse toResponse(SavingsGoal g) {
        return new SavingsGoalResponse(
                g.getId(),
                g.getName(),
                g.getIcon(),
                g.getColor(),
                g.getSavedAmount(),
                g.getTargetAmount(),
                g.getDueDate(),
                g.getNote(),
                g.getPercentFunded(),
                g.isFullyFunded()
        );
    }
}