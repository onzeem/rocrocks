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

    @PostMapping
    public ResponseEntity<BudgetSummaryResponse> createBudget(@RequestBody CreateBudgetRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + request.getUserId()));

        boolean alreadyExists = budgetRepository
                .findByUserIdAndMonthAndYear(request.getUserId(), request.getMonth(), request.getYear())
                .isPresent();
        if (alreadyExists) {
            throw new RuntimeException("Budget already exists for user " + request.getUserId()
                    + " in " + request.getMonth() + "/" + request.getYear());
        }

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

    /**
     * Adds a category limit to the given month's budget — creating that
     * month's budget first (with $0 income) if it doesn't exist yet, or
     * updating the amount in place if this category is already on it.
     *
     * Unlike createBudget above, this is safe to call repeatedly: the
     * first call for a given user/month/year creates the budget; every
     * call after that (that month, or category) just adds or updates
     * one category on whichever budget already matches.
     */
    @PostMapping("/user/{userId}/{year}/{month}/categories")
    public ResponseEntity<BudgetSummaryResponse> addOrUpdateCategory(
            @PathVariable Long userId, @PathVariable int year, @PathVariable int month,
            @RequestBody CategoryAllocationRequest allocation) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        Budget budget = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year)
                .orElseGet(() -> budgetRepository.save(new Budget(user, month, year, BigDecimal.ZERO)));

        Category category = categoryRepository.findById(allocation.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with ID: " + allocation.getCategoryId()));

        boolean updatedExisting = false;
        for (BudgetCategory bc : budget.getBudgetCategories()) {
            if (bc.getCategory().getId().equals(category.getId())) {
                bc.setAllocatedAmount(allocation.getAmount());
                updatedExisting = true;
                break;
            }
        }
        if (!updatedExisting) {
            budget.addBudgetCategory(new BudgetCategory(category, allocation.getAmount()));
        }

        Budget saved = budgetRepository.save(budget);
        return ResponseEntity.ok(toSummary(saved));
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<BudgetSummaryResponse> getSummary(@PathVariable Long id) {
        Budget budget = budgetRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Budget not found with ID: " + id));

        return ResponseEntity.ok(toSummary(budget));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BudgetSummaryResponse>> getBudgetsByUser(@PathVariable Long userId) {
        List<Budget> budgets = budgetRepository.findByUserId(userId);
        List<BudgetSummaryResponse> summaries = budgets.stream()
                .map(this::toSummary)
                .collect(Collectors.toList());

        return ResponseEntity.ok(summaries);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable Long id) {
        if (!budgetRepository.existsById(id)) {
            throw new RuntimeException("Budget not found with ID: " + id);
        }
        budgetRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

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