package com.san3a.backend.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateServiceRequest(
        @NotBlank(message = "Service name is required")
        @Size(max = 120, message = "Service name max length is 120")
        String name,

        @Size(max = 1000, message = "Description max length is 1000")
        String description
) {
}
