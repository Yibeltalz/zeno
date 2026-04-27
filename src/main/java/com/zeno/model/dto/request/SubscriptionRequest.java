package com.zeno.model.dto.request;

import com.zeno.model.entity.Subscription.BillingCycle;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

public class SubscriptionRequest {

    @Data
    public static class Create {
        @NotBlank(message = "Name is required")
        private String name;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
        private BigDecimal amount;

        private String currency = "USD";

        @NotNull(message = "Billing cycle is required")
        private BillingCycle billingCycle;

        private LocalDate nextChargeDate;

        private String category;
    }

    @Data
    public static class Update {
        private String name;
        private BigDecimal amount;
        private String currency;
        private BillingCycle billingCycle;
        private LocalDate nextChargeDate;
        private String category;
    }
}
