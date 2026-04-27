package com.zeno.model.dto.response;

import com.zeno.model.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class SubscriptionResponse {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Full {
        private UUID id;
        private String name;
        private BigDecimal amount;
        private String currency;
        private Subscription.BillingCycle billingCycle;
        private LocalDate nextChargeDate;
        private String category;
        private Subscription.Status status;
        private Integer wasteScore;
        private Subscription.DetectedVia detectedVia;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private UUID id;
        private String name;
        private BigDecimal amount;
        private Subscription.BillingCycle billingCycle;
        private Subscription.Status status;
        private Integer wasteScore;
    }
}
