package com.bank.card_service.application.port.out;

public interface PinHasher {
    String hash(String rawPin);
}
