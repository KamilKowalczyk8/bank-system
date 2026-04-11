package com.bank.payment_service.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Payment {

    private final UUID id;
    private final UUID sourceAccountId;
    private final UUID destinationAccountId;
    private final Money money;
    private final PaymentType type;
    private PaymentStatus status;
    private final LocalDateTime createdAt;

    public Payment(UUID sourceAccountId, UUID destinationAccountId, Money money, PaymentType type) {
        if (sourceAccountId.equals(destinationAccountId)) {
            throw new IllegalStateException("Konto źródłowe i docelowe nie mogą być takie same.");
        }
        this.id = UUID.randomUUID();
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.money = money;
        this.type = type;
        this.status = PaymentStatus.INITIATED;
        this.createdAt = LocalDateTime.now();
    }

    public Payment(UUID id, UUID sourceAccountId, UUID destinationAccountId, Money money, PaymentType type, PaymentStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.sourceAccountId = sourceAccountId;
        this.destinationAccountId = destinationAccountId;
        this.money = money;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public void markAsPending() {
        if (this.status != PaymentStatus.INITIATED) {
            throw new IllegalStateException("Tylko zainicjowana płatność może przejść w tryb PENDING.");
        }
        this.status = PaymentStatus.PENDING;
    }

    public void complete() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Tylko przelew w stanie PENDING może zostać zakończony.");
        }
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail() {
        if (this.status != PaymentStatus.PENDING && this.status != PaymentStatus.INITIATED) {
            throw new IllegalStateException("Nie można odrzucić przelewu, który jest już zakończony.");
        }
        this.status = PaymentStatus.FAILED;
    }

    public void rejectAsFraud() {
        if (this.status != PaymentStatus.PENDING) {
            throw new IllegalStateException("Można odrzucić tylko płatność w procesie (PENDING)");
        }
        this.status = PaymentStatus.REJECTED_FRAUD;
    }

    public UUID getId() { return id; }
    public UUID getSourceAccountId() { return sourceAccountId; }
    public UUID getDestinationAccountId() { return destinationAccountId; }
    public Money getMoney() { return money; }
    public PaymentType getType() { return type; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
