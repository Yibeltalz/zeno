package com.zeno.controller;

import com.zeno.model.dto.request.SubscriptionRequest;
import com.zeno.model.dto.response.ApiResponse;
import com.zeno.model.dto.response.SubscriptionResponse;
import com.zeno.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * LAYER 3 — Controller (Presentation Layer)
 *
 * GET    /api/subscriptions           → list all
 * POST   /api/subscriptions           → create
 * GET    /api/subscriptions/{id}      → get one
 * PUT    /api/subscriptions/{id}      → update
 * PATCH  /api/subscriptions/{id}/cancel → cancel
 * DELETE /api/subscriptions/{id}      → delete
 * GET    /api/subscriptions/dashboard → spending summary
 */
@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
@Tag(name = "Subscriptions", description = "Manage user subscriptions")
@SecurityRequirement(name = "bearerAuth")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get spending dashboard summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboard(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.getDashboard(userId)));
    }

    @GetMapping
    @Operation(summary = "List all subscriptions for the logged-in user")
    public ResponseEntity<ApiResponse<List<SubscriptionResponse.Full>>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.getAllForUser(userId)));
    }

    @PostMapping
    @Operation(summary = "Create a new subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse.Full>> create(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody SubscriptionRequest.Create request) {

        UUID userId = extractUserId(userDetails);
        var created = subscriptionService.create(userId, request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Subscription created", created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single subscription by ID")
    public ResponseEntity<ApiResponse<SubscriptionResponse.Full>> getById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {

        UUID userId = extractUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.ok(subscriptionService.getById(userId, id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse.Full>> update(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id,
            @RequestBody SubscriptionRequest.Update request) {

        UUID userId = extractUserId(userDetails);
        var updated = subscriptionService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.ok("Subscription updated", updated));
    }

    @PatchMapping("/{id}/cancel")
    @Operation(summary = "Cancel a subscription")
    public ResponseEntity<ApiResponse<SubscriptionResponse.Full>> cancel(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {

        UUID userId = extractUserId(userDetails);
        var cancelled = subscriptionService.cancel(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Subscription cancelled", cancelled));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a subscription permanently")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable UUID id) {

        UUID userId = extractUserId(userDetails);
        subscriptionService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.ok("Subscription deleted", null));
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private UUID extractUserId(UserDetails userDetails) {
        // Username stored in JWT is the user's email — look up their ID
        // For simplicity we store UUID as the subject in JwtService
        return UUID.fromString(userDetails.getUsername());
    }
}
