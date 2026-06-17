package com.san3a.backend.review.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
        Long requestId,
        Long userId,
        Long taskerId,
        Short rating,
        String comment,
        LocalDateTime createdAt
) {
}
