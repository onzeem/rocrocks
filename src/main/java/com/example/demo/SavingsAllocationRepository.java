package com.example.demo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SavingsAllocationRepository extends JpaRepository<SavingsAllocation, Long> {
    List<SavingsAllocation> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);
}