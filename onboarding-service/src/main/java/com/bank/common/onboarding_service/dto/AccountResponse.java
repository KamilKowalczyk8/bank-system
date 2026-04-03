package com.bank.common.onboarding_service.dto;

import java.math.BigDecimal;

public record AccountResponse(
        String accountId,
        String customerId,
        String accountNumber,
        BigDecimal balance,
        String currency,
        String status
) {
}
