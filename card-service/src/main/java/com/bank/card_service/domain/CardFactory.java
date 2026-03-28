package com.bank.card_service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class CardFactory {

    private final int defaultValidityYears;
    private final BigDecimal defaultDailyLimit;

    public CardFactory(int defaultValidityYears, BigDecimal defaultDailyLimit) {
        this.defaultValidityYears = defaultValidityYears;
        this.defaultDailyLimit = defaultDailyLimit;
    }

    public Card createNew(UUID accountId, CardNumber cardNumber, String pinHash) {
        return new Card(
                UUID.randomUUID(),
                cardNumber,
                accountId,
                LocalDateTime.now().plusYears(defaultValidityYears),
                pinHash,
                defaultDailyLimit
        );
    }
}
