package com.bank.card_service.application.port.out;

import java.time.LocalDateTime;

public interface CvvGenerator {
    String generate(String cardNumber, LocalDateTime expiryDate);
}
