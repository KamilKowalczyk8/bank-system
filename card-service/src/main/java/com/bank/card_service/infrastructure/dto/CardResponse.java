package com.bank.card_service.infrastructure.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CardResponse(
        UUID id,
        String cardNumber,
        String status,
        LocalDateTime expiryDate,
        BigDecimal dailyLimit,
        String cvv
) {
}
