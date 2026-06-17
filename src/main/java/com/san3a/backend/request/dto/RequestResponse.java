package com.san3a.backend.request.dto;

import com.san3a.backend.domain.enums.RequestStatus;

import java.time.LocalDateTime;

public record RequestResponse(
        Long requestId,
        Long userId,
        Long taskerId,
        Long serviceId,
        String serviceName,
        String city,
        String street,
        String block,
        String apartment,
        LocalDateTime scheduledAt,
        RequestStatus status,
        String notes,
        LocalDateTime createdAt
) {
}
