package com.zeno.service;

import com.zeno.exception.ZenoException;
import com.zeno.model.entity.Transaction;
import com.zeno.model.entity.User;
import com.zeno.repository.TransactionRepository;
import com.zeno.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * LAYER 2 — Service (Business Logic Layer)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Transaction> getAll(UUID userId) {
        return transactionRepository.findByUserIdOrderByDateDesc(userId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getRecurring(UUID userId) {
        return transactionRepository.findByUserIdAndIsRecurringTrue(userId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getByDateRange(UUID userId, LocalDate from, LocalDate to) {
        return transactionRepository.findByUserIdAndDateBetweenOrderByDateDesc(userId, from, to);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSpendingSummary(UUID userId, LocalDate from, LocalDate to) {
        List<Transaction> transactions = transactionRepository
                .findByUserIdAndDateBetweenOrderByDateDesc(userId, from, to);

        BigDecimal total = transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long recurringCount = transactions.stream()
                .filter(Transaction::getIsRecurring)
                .count();

        BigDecimal recurringTotal = transactions.stream()
                .filter(Transaction::getIsRecurring)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalSpend", total);
        summary.put("transactionCount", transactions.size());
        summary.put("recurringCount", recurringCount);
        summary.put("recurringTotal", recurringTotal);
        summary.put("from", from);
        summary.put("to", to);
        return summary;
    }

    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ZenoException.NotFound("User not found: " + userId));
    }
}
