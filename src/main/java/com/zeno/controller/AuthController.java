package com.zeno.controller;

import com.zeno.model.dto.request.AuthRequest;
import com.zeno.model.dto.response.ApiResponse;
import com.zeno.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * LAYER 3 — Controller (Presentation Layer)
 * Handles HTTP in/out only. No business logic here.
 *
 * POST /api/auth/register  → create account
 * POST /api/auth/login     → get JWT token
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Register and login endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<Object>> register(
            @Valid @RequestBody AuthRequest.Register request) {

        var result = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Account created successfully", result));
    }

    @PostMapping("/login")
    @Operation(summary = "Login and receive a JWT token")
    public ResponseEntity<ApiResponse<Object>> login(
            @Valid @RequestBody AuthRequest.Login request) {

        var result = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", result));
    }
}
