package com.bank.card_service.application.service;

import com.bank.card_service.domain.Card;
import com.bank.card_service.domain.CardRepository;

import java.util.UUID;

public class ActivateCardUseCase {

    private final CardRepository cardRepository;

    public ActivateCardUseCase(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    public void activate(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalStateException("Karta o podanym ID nie istnieje."));

        card.activate();

        cardRepository.save(card);
    }

}
