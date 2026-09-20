package com.example.demo;

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
@RequestMapping("/api/transactions")
public class TransactionController {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public TransactionController(TransactionRepository transactionRepository, UserRepository userRepository, CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody CreateTransactionRequest transactionRequest) {
        User user = userRepository.findById(transactionRequest.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + transactionRequest.getUserId()));

        TransactionType type = transactionRequest.getType() != null ? transactionRequest.getType() : TransactionType.EXPENSE;

        // Category is required for expenses (what was this money spent
        // on?), but doesn't apply to income — a paycheck isn't "in" any
        // spending category, so it's left null for INCOME transactions
        // even if a categoryId happened to be sent.
        Category category = null;
        if (type == TransactionType.EXPENSE) {
            if (transactionRequest.getCategoryId() == null) {
                throw new RuntimeException("categoryId is required for an EXPENSE transaction");
            }
            category = categoryRepository.findById(transactionRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with ID: " + transactionRequest.getCategoryId()));
        }

        Transaction transaction = new Transaction(user, category, transactionRequest.getAmount(),
                transactionRequest.getDescription(), transactionRequest.getDate(), type);
        Transaction saved = transactionRepository.save(transaction);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(saved));
    }
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getByUser(@PathVariable Long userId) {
        List<TransactionResponse> results = transactionRepository.findByUserId(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
    @GetMapping("/user/{userId}/month/{year}/{month}")
    public ResponseEntity<List<TransactionResponse>> getByUserAndMonth(
            @PathVariable Long userId, @PathVariable int year, @PathVariable int month) {
        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<TransactionResponse> results = transactionRepository
                .findByUserIdAndDateBetween(userId, start, end).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(results);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new RuntimeException("Transaction not found with ID: " + id);
        }
        transactionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
    private TransactionResponse toResponse(Transaction t) {
        String categoryName = t.getCategory() != null ? t.getCategory().getName() : null;
        return new TransactionResponse(t.getId(), categoryName, t.getAmount(),
               t.getDescription(), t.getDate(), t.getType());
    }
}