package com.bank.payment_service.infrastructure.out.messaging;

import com.bank.payment_service.domain.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentFailedEvent(
        UUID paymentId,
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        String failureReason
) {
}
