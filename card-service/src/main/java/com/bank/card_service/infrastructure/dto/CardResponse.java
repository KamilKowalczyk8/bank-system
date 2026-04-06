package com.bank.card_service.infrastructure.dto;

import com.bank.card_service.domain.Card;

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
    public static CardResponse fromDomain(Card card, String rawCvv) {
        return new CardResponse(
                card.getId(),
                card.getCardNumber().getValue(),
                card.getStatus().name(),
                card.getExpiryDate(),
                card.getDailyLimit(),
                rawCvv
        );
    }
}
