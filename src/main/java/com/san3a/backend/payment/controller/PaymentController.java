package com.san3a.backend.payment.controller;

import com.san3a.backend.common.api.ApiResponse;
import com.san3a.backend.payment.dto.CreatePaymentRequest;
import com.san3a.backend.payment.dto.PaymentResponse;
import com.san3a.backend.payment.service.PaymentService;
import com.san3a.backend.security.AppUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<ApiResponse<PaymentResponse>> createPayment(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Payment created successfully", paymentService.createPayment(principal, request)));
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPaymentByRequest(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Payment fetched successfully", paymentService.getByRequestId(principal, requestId)));
    }

    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyUserPayments(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.ok("User payments fetched successfully", paymentService.getMyUserPayments(principal)));
    }

    @GetMapping("/tasker/me")
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> getMyTaskerPayments(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Tasker payments fetched successfully", paymentService.getMyTaskerPayments(principal)));
    }
}
