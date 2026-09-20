package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BudgetCategoryRepository extends JpaRepository<BudgetCategory, Long> {
    List<BudgetCategory> findByBudgetId(Long budgetId);
}