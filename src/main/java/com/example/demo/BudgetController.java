package com.example.demo;
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
@RequestMapping("/api/budgets")
public class BudgetController {

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public BudgetController(BudgetRepository budgetRepository,
                            UserRepository userRepository,
                            CategoryRepository categoryRepository) {
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    // CREATE a new budget
    @PostMapping
    public ResponseEntity<BudgetSummaryResponse> createBudget(@RequestBody CreateBudgetRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        Budget budget = new Budget(user, request.getMonth(), request.getYear(), request.getTotalIncome());

        if (request.getCategories() != null) {
            for (CategoryAllocationRequest allocation : request.getCategories()) {
                Category category = categoryRepository.findById(allocation.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found with ID: " + allocation.getCategoryId()));
                budget.addBudgetCategory(new BudgetCategory(category, allocation.getAmount()));
            }
        }

        Budget saved = budgetRepository.save(budget);
        return ResponseEntity.status(HttpStatus.CREATED).body(toSummary(saved));
    }

    // READ a single budget summary by ID
    @GetMapping("/{id}/summary")
    public ResponseEntity<BudgetSummaryResponse> getSummary(@PathVariable Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + id));

        return ResponseEntity.ok(toSummary(budget));
    }

    // READ all budgets for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetSummaryResponse>> getBudgetsByUser(@PathVariable Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        List<BudgetSummaryResponse> summaries = budgets.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        return ResponseEntity.ok(summaries);
    }

    // DELETE a budget by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new RuntimeException("Budget not found with ID: " + id);
        }
        budgetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // Helper method to convert Budget entity to DTO
    private BudgetSummaryResponse toSummary(Budget budget) {
        List<CategorySummaryItem> items = budget.getBudgetCategories().stream()
                .map(bc -> new CategorySummaryItem(bc.getCategory().getName(), bc.getAllocatedAmount()))
                .collect(Collectors.toList());

        return new BudgetSummaryResponse(
                budget.getId(),
                budget.getTotalIncome(),
                budget.getTotalAllocated(),
                budget.getRemainingIncome(),
                budget.isOverAllocated(),
                items
        );
    }
}