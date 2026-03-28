package com.bank.card_service.domain;

public class CardNumber {

    private final String value;

    public CardNumber(String value) {
        if (value == null || !value.matches("\\d{16}")) {
            throw new IllegalArgumentException("Nieprawidłowy numer karty. Musi składać się z 16 cyfr.");
        }

        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
