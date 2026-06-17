package com.san3a.backend.payment.dto;

import com.san3a.backend.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long requestId,
        BigDecimal amount,
        PaymentMethod method,
        String transactionRef,
        LocalDateTime paidAt
) {
}
