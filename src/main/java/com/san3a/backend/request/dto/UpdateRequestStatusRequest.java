package com.san3a.backend.request.dto;

import com.san3a.backend.domain.enums.RequestStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateRequestStatusRequest(
        @NotNull(message = "Status is required")
        RequestStatus status
) {
}
