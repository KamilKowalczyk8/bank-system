package com.bank.card_service.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CardFactoryTest {

    private final CardFactory cardFactory = new CardFactory(3, new BigDecimal("5000.00"));

    @Test
    void shouldCreateNewCardSuccesfully() {

        UUID accountId = UUID.randomUUID();
        CardNumber safeCardNumber = new CardNumber("1234567890123456");        String hashedPin = "hashed_secret_pin_123";
        LocalDateTime expiryDate = LocalDateTime.now().plusYears(3);
        String cvv = "123";

        Card newCard = cardFactory.createNew(
                accountId,
                safeCardNumber,
                hashedPin,
                expiryDate,
                cvv
        );

        assertNotNull(newCard.getId(), "ID karty nie powinno być nullem");
        assertEquals(accountId, newCard.getAccountId(), "Account ID powinno się zgadzać");
        assertEquals(CardStatus.CREATED, newCard.getStatus(), "Nowa karta powinna mieć status CREATED");
        assertEquals(new BigDecimal("5000.00"), newCard.getDailyLimit(), "Domyślny limit powinien wynosić 5000.00");
    }
}
