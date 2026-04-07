package com.bank.card_service.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Card {

    private final UUID id;
    private final CardNumber cardNumber;
    private final UUID accountId;
    private CardStatus status;
    private final LocalDateTime expiryDate;
    private final String pinHash;
    private BigDecimal dailyLimit;
    private final String cvv;

    public Card(UUID id, CardNumber cardNumber, UUID accountId, LocalDateTime expiryDate, String pinHash, BigDecimal dailyLimit, String cvv) {
        if (dailyLimit == null || dailyLimit.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Limit dzienny karty musi być większy niż zero!");
        }
        if (expiryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data ważności nowej karty nie może być w przeszłości!");
        }

        this.id = id;
        this.cardNumber = cardNumber;
        this.accountId = accountId;
        this.status = CardStatus.CREATED;
        this.expiryDate = expiryDate;
        this.pinHash = pinHash;
        this.dailyLimit = dailyLimit;
        this.cvv = cvv;
    }

    public void block() {
        if (this.status == CardStatus.BLOCKED) {
            throw new IllegalStateException("Karta jest już zablokowana!");
        }
        if (this.status == CardStatus.EXPIRED) {
            throw new IllegalStateException("Nie można zablokować wygasłej karty!");
        }
        this.status = CardStatus.BLOCKED;
    }

    public void activate() {
        if (this.status != CardStatus.ACTIVE) {
            throw new IllegalStateException("Karta jest już aktywna");
        }
        if (this.status != CardStatus.CREATED) {
            throw new IllegalStateException("Można aktywować tylko nowo utworzoną kartę!");
        }
        this.status = CardStatus.ACTIVE;
    }

    public UUID getId() { return id; }
    public CardNumber getCardNumber() { return cardNumber; }
    public UUID getAccountId() { return accountId; }
    public CardStatus getStatus() { return status; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public String getPinHash() { return pinHash; }
    public BigDecimal getDailyLimit() { return dailyLimit; }
    public String getCvv() { return cvv; }

}
