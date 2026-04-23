package com.bank.card_service.application.service;

import com.bank.card_service.domain.Card;
import com.bank.card_service.domain.CardRepository;
import com.bank.common.api.ErrorReporter;

import java.util.UUID;

public class ActivateCardUseCase {

    private final CardRepository cardRepository;
    private final ErrorReporter errorReporter;

    public ActivateCardUseCase(CardRepository cardRepository, ErrorReporter errorReporter) {
        this.cardRepository = cardRepository;
        this.errorReporter = errorReporter;
    }

    public void activate(UUID cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> {
                    String msg = "OSTRZEŻENIE: Próba aktywacji nieistniejącej karty o ID: " + cardId;
                    errorReporter.report(new IllegalStateException(msg));
                    return new IllegalStateException("Karta o podanym ID nie istnieje.");
                });

        card.activate();

        cardRepository.save(card);
    }

}
