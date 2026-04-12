package com.bank.fraud_service.infrastructure.in.web;

import java.math.BigDecimal;
import java.util.UUID;

public record FraudRequestDto(
        UUID paymentId,
        BigDecimal amount,
        String currency,
        UUID sourceAccount,
        UUID destinationAccountId
) {}
