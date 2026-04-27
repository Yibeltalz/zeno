package com.zeno.service;

import com.zeno.exception.ZenoException;
import com.zeno.model.dto.request.SubscriptionRequest;
import com.zeno.model.dto.response.SubscriptionResponse;
import com.zeno.model.entity.Subscription;
import com.zeno.model.entity.User;
import com.zeno.repository.SubscriptionRepository;
import com.zeno.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * LAYER 2 — Service (Business Logic Layer)
 * All subscription business rules live here.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    // ── Create ────────────────────────────────────────────────────────────────
    @Transactional
    public SubscriptionResponse.Full create(UUID userId, SubscriptionRequest.Create request) {
        log.info("Creating subscription '{}' for user {}", request.getName(), userId);

        User user = findUser(userId);

        Subscription subscription = Subscription.builder()
                .user(user)
                .name(request.getName())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .billingCycle(request.getBillingCycle())
                .nextChargeDate(request.getNextChargeDate())
                .category(request.getCategory())
                .build();

        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription created: {}", subscription.getId());
        return toFull(subscription);
    }

    // ── Get All for User ──────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public List<SubscriptionResponse.Full> getAllForUser(UUID userId) {
        return subscriptionRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toFull)
                .collect(Collectors.toList());
    }

    // ── Get One ───────────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public SubscriptionResponse.Full getById(UUID userId, UUID subscriptionId) {
        return toFull(findSubscriptionForUser(userId, subscriptionId));
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @Transactional
    public SubscriptionResponse.Full update(UUID userId, UUID subscriptionId,
                                             SubscriptionRequest.Update request) {
        Subscription subscription = findSubscriptionForUser(userId, subscriptionId);

        if (request.getName() != null)          subscription.setName(request.getName());
        if (request.getAmount() != null)        subscription.setAmount(request.getAmount());
        if (request.getCurrency() != null)      subscription.setCurrency(request.getCurrency());
        if (request.getBillingCycle() != null)  subscription.setBillingCycle(request.getBillingCycle());
        if (request.getNextChargeDate() != null) subscription.setNextChargeDate(request.getNextChargeDate());
        if (request.getCategory() != null)      subscription.setCategory(request.getCategory());

        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription updated: {}", subscriptionId);
        return toFull(subscription);
    }

    // ── Cancel ────────────────────────────────────────────────────────────────
    @Transactional
    public SubscriptionResponse.Full cancel(UUID userId, UUID subscriptionId) {
        Subscription subscription = findSubscriptionForUser(userId, subscriptionId);
        subscription.setStatus(Subscription.Status.CANCELLED);
        subscription = subscriptionRepository.save(subscription);
        log.info("Subscription cancelled: {}", subscriptionId);
        return toFull(subscription);
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @Transactional
    public void delete(UUID userId, UUID subscriptionId) {
        Subscription subscription = findSubscriptionForUser(userId, subscriptionId);
        subscriptionRepository.delete(subscription);
        log.info("Subscription deleted: {}", subscriptionId);
    }

    // ── Dashboard Summary ─────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public Map<String, Object> getDashboard(UUID userId) {
        BigDecimal monthlyTotal = subscriptionRepository.sumMonthlyActiveByUserId(userId);
        BigDecimal yearlyTotal  = subscriptionRepository.sumYearlyActiveByUserId(userId);
        long activeCount = subscriptionRepository.countByUserIdAndStatus(userId, Subscription.Status.ACTIVE);

        // Estimate annual spending
        BigDecimal annualEstimate = monthlyTotal.multiply(BigDecimal.valueOf(12)).add(yearlyTotal);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("monthlySpend", monthlyTotal);
        dashboard.put("yearlySpend", yearlyTotal);
        dashboard.put("annualEstimate", annualEstimate);
        dashboard.put("activeSubscriptions", activeCount);

        // Top wasted subscriptions (waste_score > 50)
        List<SubscriptionResponse.Summary> wasted = subscriptionRepository
                .findByUserIdAndStatus(userId, Subscription.Status.ACTIVE)
                .stream()
                .filter(s -> s.getWasteScore() > 50)
                .map(this::toSummary)
                .collect(Collectors.toList());
        dashboard.put("wastedSubscriptions", wasted);

        return dashboard;
    }

    // ── Private helpers ───────────────────────────────────────────────────────
    private User findUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ZenoException.NotFound("User not found: " + userId));
    }

    private Subscription findSubscriptionForUser(UUID userId, UUID subscriptionId) {
        return subscriptionRepository.findByIdAndUserId(subscriptionId, userId)
                .orElseThrow(() -> new ZenoException.NotFound(
                        "Subscription not found: " + subscriptionId));
    }

    private SubscriptionResponse.Full toFull(Subscription s) {
        return SubscriptionResponse.Full.builder()
                .id(s.getId())
                .name(s.getName())
                .amount(s.getAmount())
                .currency(s.getCurrency())
                .billingCycle(s.getBillingCycle())
                .nextChargeDate(s.getNextChargeDate())
                .category(s.getCategory())
                .status(s.getStatus())
                .wasteScore(s.getWasteScore())
                .detectedVia(s.getDetectedVia())
                .createdAt(s.getCreatedAt())
                .build();
    }

    private SubscriptionResponse.Summary toSummary(Subscription s) {
        return SubscriptionResponse.Summary.builder()
                .id(s.getId())
                .name(s.getName())
                .amount(s.getAmount())
                .billingCycle(s.getBillingCycle())
                .status(s.getStatus())
                .wasteScore(s.getWasteScore())
                .build();
    }
}
