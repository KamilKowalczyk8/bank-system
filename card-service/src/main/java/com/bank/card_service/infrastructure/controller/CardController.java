package com.bank.card_service.infrastructure.controller;

import com.bank.card_service.application.service.ActivateCardUseCase;
import com.bank.card_service.application.service.CreateCardResult;
import com.bank.card_service.application.service.CreateCardUseCase;
import com.bank.card_service.infrastructure.dto.CardResponse;
import com.bank.card_service.infrastructure.dto.CreateCardRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CreateCardUseCase createCardUseCase;
    private final ActivateCardUseCase activateCardUseCase;

    public CardController(CreateCardUseCase createCardUseCase, ActivateCardUseCase activateCardUseCase) {
        this.createCardUseCase = createCardUseCase;
        this.activateCardUseCase = activateCardUseCase;
    }

    @PostMapping
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CreateCardRequest request
    ) {
        CreateCardResult result = createCardUseCase.execute(request.accountId(), request.pin());

        CardResponse response = CardResponse.fromDomain(result.card(), result.rawCvv());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateCard(@PathVariable UUID id) {
        activateCardUseCase.activate(id);

        return ResponseEntity.noContent().build();
    }

}
