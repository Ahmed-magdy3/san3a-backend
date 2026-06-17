package com.san3a.backend.auth.dto;

import com.san3a.backend.domain.enums.AccountRole;

public record AuthResponse(
        String accessToken,
        String tokenType,
        Long actorId,
        AccountRole role,
        String email
) {
}
