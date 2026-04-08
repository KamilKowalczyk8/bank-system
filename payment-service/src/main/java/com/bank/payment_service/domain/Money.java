package com.bank.payment_service.domain;

import java.math.BigDecimal;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        Objects.requireNonNull(amount, "Kwota nie może być nullem");
        Objects.requireNonNull(currency, "Waluta nie może być nullem");

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Kwota musi być większa od zera");
        }
    }
}
