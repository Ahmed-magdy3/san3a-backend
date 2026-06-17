package com.san3a.backend.payment.dto;

import com.san3a.backend.domain.enums.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull(message = "Request id is required")
        Long requestId,

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
        BigDecimal amount,

        @NotNull(message = "Payment method is required")
        PaymentMethod method,

        @Size(max = 120, message = "Transaction reference max length is 120")
        String transactionRef
) {
}
