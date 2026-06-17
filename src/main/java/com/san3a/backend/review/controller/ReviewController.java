package com.san3a.backend.review.controller;

import com.san3a.backend.common.api.ApiResponse;
import com.san3a.backend.review.dto.CreateReviewRequest;
import com.san3a.backend.review.dto.ReviewResponse;
import com.san3a.backend.review.dto.TaskerReviewSummaryResponse;
import com.san3a.backend.review.service.ReviewService;
import com.san3a.backend.security.AppUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @Valid @RequestBody CreateReviewRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Review created successfully", reviewService.createReview(principal, request)));
    }

    @GetMapping("/request/{requestId}")
    public ResponseEntity<ApiResponse<ReviewResponse>> getReviewByRequest(
            @AuthenticationPrincipal AppUserPrincipal principal,
            @PathVariable Long requestId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Review fetched successfully", reviewService.getByRequestId(principal, requestId)));
    }

    @GetMapping("/user/me")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyUserReviews(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.ok("User reviews fetched successfully", reviewService.getMyUserReviews(principal)));
    }

    @GetMapping("/tasker/me")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getMyTaskerReviews(
            @AuthenticationPrincipal AppUserPrincipal principal
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Tasker reviews fetched successfully", reviewService.getMyTaskerReviews(principal)));
    }

    @GetMapping("/tasker/{taskerId}")
    public ResponseEntity<ApiResponse<TaskerReviewSummaryResponse>> getTaskerSummary(
            @PathVariable Long taskerId
    ) {
        return ResponseEntity.ok(ApiResponse.ok("Tasker review summary fetched successfully", reviewService.getTaskerSummary(taskerId)));
    }
}
