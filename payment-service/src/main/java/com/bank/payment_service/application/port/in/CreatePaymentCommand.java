package com.bank.payment_service.application.port.in;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentCommand(
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        String currency,
        String type
) {}
