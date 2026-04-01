package com.bank.card_service.application.service;

import com.bank.card_service.application.port.out.*;
import com.bank.card_service.domain.Card;
import com.bank.card_service.domain.CardFactory;
import com.bank.card_service.domain.CardNumber;
import com.bank.card_service.domain.CardRepository;
import com.bank.card_service.infrastructure.dto.event.CardCreatedEvent;

import java.time.LocalDateTime;
import java.util.UUID;

public class CreateCardUseCase {

    private final CardRepository cardRepository;
    private final CardNumberGenerator cardNumberGenerator;
    private final PinHasher pinHasher;
    private final CardFactory cardFactory;
    private final CvvGenerator cvvGenerator;
    private final CardEventPublisher cardEventPublisher;
    private final CustomerProvider customerProvider;

    public CreateCardUseCase(
        CardRepository cardRepository,
        CardNumberGenerator cardNumberGenerator,
        PinHasher pinHasher,
        CardFactory cardFactory,
        CvvGenerator cvvGenerator,
        CardEventPublisher cardEventPublisher,
        CustomerProvider customerProvider
    ) {
        this.cardRepository = cardRepository;
        this.cardNumberGenerator = cardNumberGenerator;
        this.pinHasher = pinHasher;
        this.cardFactory = cardFactory;
        this.cvvGenerator = cvvGenerator;
        this.cardEventPublisher = cardEventPublisher;
        this.customerProvider = customerProvider;
    }

    public Card execute(UUID accountId, String rawPin) {
        String rawCardNumber = cardNumberGenerator.generate();

        CardNumber safeCardNumber = new CardNumber(rawCardNumber);

        if (cardRepository.existsByCardNumber(safeCardNumber)) {
            throw new IllegalStateException("Krytyczny błąd: Wygenerowano numer karty, który już istnieje w systemie!");
        }

        String hashedPin = pinHasher.hash(rawPin);

        LocalDateTime expiryDate = cardFactory.calculateExpiryDate();
        String generatedCvv = cvvGenerator.generate(safeCardNumber.getValue(), expiryDate);

        Card newCard = cardFactory.createNew(accountId, safeCardNumber, hashedPin, expiryDate, generatedCvv);

        Card savedCard = cardRepository.save(newCard);

        String customerEmail = customerProvider.getCustomerEmail(accountId);

        CardCreatedEvent event = new CardCreatedEvent(
                savedCard.getId(),
                savedCard.getAccountId(),
                maskCardNumber(savedCard.getCardNumber().getValue()),
                customerEmail,
                LocalDateTime.now()
        );

        cardEventPublisher.publishCardCreated(event);

        return savedCard;
    }


    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) return cardNumber;
        return "**** **** **** " + cardNumber.substring(cardNumber.length() - 4);
    }
}
