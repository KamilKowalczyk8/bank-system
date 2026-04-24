package com.bank.common.notification_service.event;

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
