package com.san3a.backend.catalog.dto;

public record ServiceResponse(
        Long serviceId,
        String name,
        String description
) {
}
