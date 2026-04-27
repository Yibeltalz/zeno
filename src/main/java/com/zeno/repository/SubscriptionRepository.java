package com.zeno.repository;

import com.zeno.model.entity.Subscription;
import com.zeno.model.entity.Subscription.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * LAYER 1 — Repository (Data Access Layer)
 */
@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    List<Subscription> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Subscription> findByUserIdAndStatus(UUID userId, Status status);

    Optional<Subscription> findByIdAndUserId(UUID id, UUID userId);

    long countByUserIdAndStatus(UUID userId, Status status);

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Subscription s " +
           "WHERE s.user.id = :userId AND s.status = 'ACTIVE' AND s.billingCycle = 'MONTHLY'")
    BigDecimal sumMonthlyActiveByUserId(@Param("userId") UUID userId);

    @Query("SELECT COALESCE(SUM(s.amount), 0) FROM Subscription s " +
           "WHERE s.user.id = :userId AND s.status = 'ACTIVE' AND s.billingCycle = 'YEARLY'")
    BigDecimal sumYearlyActiveByUserId(@Param("userId") UUID userId);
}
