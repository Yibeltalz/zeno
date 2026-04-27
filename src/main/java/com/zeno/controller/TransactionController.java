package com.zeno.controller;

import com.zeno.model.dto.response.ApiResponse;
import com.zeno.model.entity.Transaction;
import com.zeno.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * LAYER 3 — Controller (Presentation Layer)
 *
 * GET /api/transactions               → all transactions
 * GET /api/transactions/recurring     → recurring only
 * GET /api/transactions/summary       → spending summary ?from=&to=
 */
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transactions", description = "View and analyze transactions")
@SecurityRequirement(name = "bearerAuth")
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    @Operation(summary = "Get all transactions")
    public ResponseEntity<ApiResponse<List<Transaction>>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(transactionService.getAll(userId)));
    }

    @GetMapping("/recurring")
    @Operation(summary = "Get recurring transactions only")
    public ResponseEntity<ApiResponse<List<Transaction>>> getRecurring(
            @AuthenticationPrincipal UserDetails userDetails) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(transactionService.getRecurring(userId)));
    }

    @GetMapping("/summary")
    @Operation(summary = "Get spending summary for a date range")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        UUID userId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.ok(
                transactionService.getSpendingSummary(userId, from, to)));
    }
}
