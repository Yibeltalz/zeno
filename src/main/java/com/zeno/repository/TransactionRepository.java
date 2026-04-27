package com.zeno.repository;

import com.zeno.model.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * LAYER 1 — Repository (Data Access Layer)
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByUserIdOrderByDateDesc(UUID userId);

    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(
            UUID userId, LocalDate from, LocalDate to);

    List<Transaction> findByUserIdAndIsRecurringTrue(UUID userId);

    Optional<Transaction> findByIdAndUserId(UUID id, UUID userId);

    boolean existsByPlaidTransactionId(String plaidTransactionId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId " +
           "AND LOWER(t.merchantName) LIKE LOWER(CONCAT('%', :merchant, '%'))")
    List<Transaction> findByUserIdAndMerchantContaining(
            @Param("userId") UUID userId,
            @Param("merchant") String merchant);
}
