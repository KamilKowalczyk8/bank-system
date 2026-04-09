package com.bank.payment_service.application.port.in;

import com.bank.payment_service.domain.Currency;
import com.bank.payment_service.domain.PaymentType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentCommand(
        UUID sourceAccountId,
        UUID destinationAccountId,
        BigDecimal amount,
        Currency currency,
        PaymentType type
) {}
