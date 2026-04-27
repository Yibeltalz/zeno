package com.zeno.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

// ── Auth Response ──────────────────────────────────────────────────────────────
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AuthResponse {
    private String token;
    @Builder.Default
    private String type = "Bearer";
    private UUID userId;
    private String name;
    private String email;
}

// ── API Response wrapper ───────────────────────────────────────────────────────
// Use this for all API responses so Postman always gets consistent JSON
