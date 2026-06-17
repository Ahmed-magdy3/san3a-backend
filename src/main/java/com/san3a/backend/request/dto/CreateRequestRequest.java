package com.san3a.backend.request.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateRequestRequest(
        @NotNull(message = "Service id is required")
        Long serviceId,

        @Size(max = 100, message = "City max length is 100")
        String city,

        @Size(max = 150, message = "Street max length is 150")
        String street,

        @Size(max = 50, message = "Block max length is 50")
        String block,

        @Size(max = 50, message = "Apartment max length is 50")
        String apartment,

        LocalDateTime scheduledAt,

        @Size(max = 2000, message = "Notes max length is 2000")
        String notes
) {
}
