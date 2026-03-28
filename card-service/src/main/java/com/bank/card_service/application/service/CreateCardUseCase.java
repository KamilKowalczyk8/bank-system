package com.bank.card_service.application.service;

import com.bank.card_service.application.port.out.CardNumberGenerator;
import com.bank.card_service.application.port.out.PinHasher;
import com.bank.card_service.domain.Card;
import com.bank.card_service.domain.CardFactory;
import com.bank.card_service.domain.CardNumber;
import com.bank.card_service.domain.CardRepository;

import java.util.UUID;

public class CreateCardUseCase {

    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final PinHasher pinHasher;
    private final CardFactory cardFactory;

    public CreateCardUseCase(
        CardRepository cardRepository,
        CardNumberGenerator cardNumberGenerator,
        PinHasher pinHasher,
        CardFactory cardFactory
    ) {
        this.cardRepository = cardRepository;
        this.cardNumberGenerator = cardNumberGenerator;
        this.pinHasher = pinHasher;
        this.cardFactory = cardFactory;
    }

    public Card execute(UUID accountId, String rawPin) {
        String rawCardNumber = cardNumberGenerator.generate();

        CardNumber safeCardNumber = new CardNumber(rawCardNumber);

        if (cardRepository.existsByCardNumber(safeCardNumber)) {
            throw new IllegalStateException("Krytyczny błąd: Wygenerowano numer karty, który już istnieje w systemie!");
        }

        String hashedPin = pinHasher.hash(rawPin);

        Card newCard = cardFactory.createNew(accountId, safeCardNumber, hashedPin);

        return cardRepository.save(newCard);
    }
}
