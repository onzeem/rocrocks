package com.example.demo;

import java.math.BigDecimal;
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

    public SavingsGoalController(SavingsGoalRepository savingsGoalRepository, UserRepository userRepository) {
        this.savingsGoalRepository = savingsGoalRepository;
        this.userRepository = userRepository;
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
     * Adds money toward an existing goal — e.g. "I just put $50 toward
     * my Iceland trip." Increments savedAmount in place rather than
     * requiring the whole goal to be recreated or replaced.
     */
    @PostMapping("/{id}/allocate")
    public ResponseEntity<SavingsGoalResponse> allocateFunds(
            @PathVariable Long id, @RequestBody AllocateFundsRequest request) {

        SavingsGoal goal = savingsGoalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Savings goal not found with ID: " + id));

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("amount must be greater than 0");
        }

        goal.addFunds(request.getAmount());
        SavingsGoal saved = savingsGoalRepository.save(goal);
        return ResponseEntity.ok(toResponse(saved));
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