package com.san3a.backend.auth.controller;

import com.san3a.backend.auth.dto.*;
import com.san3a.backend.auth.service.AuthService;
import com.san3a.backend.common.api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/user")
    public ResponseEntity<ApiResponse<AuthResponse>> registerUser(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("User registered successfully", authService.registerUser(request)));
    }

    @PostMapping("/register/tasker")
    public ResponseEntity<ApiResponse<AuthResponse>> registerTasker(@Valid @RequestBody RegisterTaskerRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Tasker registered successfully", authService.registerTasker(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authService.login(request)));
    }
}
