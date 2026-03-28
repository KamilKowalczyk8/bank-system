package com.bank.common.account_service.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public class Account {

    private final UUID accountId;
    private final String customerId;
    private final AccountNumber accountNumber;
    private BigDecimal balance;
    private final Currency currency;
    private AccountStatus status;
    private final Instant createdAt;

    public Account(String customerId, AccountNumber accountNumber, Currency currency) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("ID klienta nie może być puste.");
        }

        this.accountId = UUID.randomUUID();
        this.customerId = customerId;
        this.accountNumber = Objects.requireNonNull(accountNumber, "Numer konta nie może być null");        this.balance = BigDecimal.ZERO;
        this.currency = currency != null ? currency : Currency.PLN;
        this.status = AccountStatus.ACTIVE;
        this.createdAt = Instant.now();
    }

    public void deposit(BigDecimal amount) {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Odmowa. Konto nie jest aktywne (Status: " + this.status + ").");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Kwota wypłaty musi być większa od zera.");
        }
        this.balance = this.balance.add(amount);
    }

    public void withdraw(BigDecimal amount) {
        if (this.status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Odmowa. Konto nie jest aktywne (Status: " + this.status + ").");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Kwota wypłaty musi być większa od zera.");
        }
        if (this.balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Odmowa. Niewystarczające środki na rachunku.");
        }
        this.balance = this.balance.subtract(amount);
    }

    public void closeAccount() {
        if (this.balance.compareTo(BigDecimal.ZERO) > 0) {
            throw new IllegalStateException("Odmowa. Nie można zamknąć konta, na którym znajdują się środki.");
        }
        this.status = AccountStatus.BLOCKED;
    }


    public UUID getAccountId() {
        return accountId;
    }
    public String getCustomerId() { return customerId; }
    public AccountNumber getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public Currency getCurrency() { return currency; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

}
