package com.san3a.backend.request.controller;

import com.san3a.backend.common.api.ApiResponse;
import com.san3a.backend.request.dto.CreateRequestRequest;
import com.san3a.backend.request.dto.RequestResponse;
import com.san3a.backend.request.dto.UpdateRequestStatusRequest;
import com.san3a.backend.request.service.RequestService;
import com.san3a.backend.security.AppUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestService requestService;

    @PostMapping("/user")
    public ResponseEntity<ApiResponse<RequestResponse>> createRequest(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody CreateRequestRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Request created successfully", requestService.create(principal, request)));
    }

    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getMyUserRequests(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.ok("User requests fetched successfully", requestService.getMyUserRequests(principal)));
    }

    @GetMapping("/tasker/me")
    public ResponseEntity<ApiResponse<List<RequestResponse>>> getMyTaskerRequests(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Tasker requests fetched successfully", requestService.getMyTaskerRequests(principal)));
    }

    @PatchMapping("/tasker/{requestId}/accept")
    public ResponseEntity<ApiResponse<RequestResponse>> acceptRequest(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Request accepted successfully", requestService.acceptRequest(principal, requestId)));
    }

    @PatchMapping("/tasker/{requestId}/status")
    public ResponseEntity<ApiResponse<RequestResponse>> updateStatus(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long requestId,
            @Valid @RequestBody UpdateRequestStatusRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Request status updated successfully", requestService.updateStatus(principal, requestId, request)));
    }
}
