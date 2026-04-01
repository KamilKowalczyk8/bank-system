package com.bank.card_service.infrastructure.controller;

import com.bank.card_service.application.service.CreateCardUseCase;
import com.bank.card_service.domain.Card;
import com.bank.card_service.infrastructure.dto.CardResponse;
import com.bank.card_service.infrastructure.dto.CreateCardRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CreateCardUseCase createCardUseCase;

    public CardController(CreateCardUseCase createCardUseCase) {
        this.createCardUseCase = createCardUseCase;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CreateCardRequest request
    ) {
        Card newCard = createCardUseCase.execute(request.accountId(), request.pin());

        CardResponse cardResponse = new CardResponse(
                newCard.getId(),
                newCard.getCardNumber().getValue(),
                newCard.getStatus().name(),
                newCard.getExpiryDate(),
                newCard.getDailyLimit(),
                newCard.getCvv()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(cardResponse);

    }
}
