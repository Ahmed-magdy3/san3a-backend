package com.san3a.backend.review.dto;

import java.util.List;

public record TaskerReviewSummaryResponse(
        Long taskerId,
        Double averageRating,
        Long totalReviews,
        List<ReviewResponse> reviews
) {
}
