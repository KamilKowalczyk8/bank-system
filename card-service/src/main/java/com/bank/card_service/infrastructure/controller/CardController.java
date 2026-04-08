package com.bank.card_service.infrastructure.controller;

import com.bank.card_service.application.service.ActivateCardUseCase;
import com.bank.card_service.application.service.CreateCardResult;
import com.bank.card_service.application.service.CreateCardUseCase;
import com.bank.card_service.infrastructure.dto.CardResponse;
import com.bank.card_service.infrastructure.dto.CreateCardRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cards")
@Tag(name = "Cards API", description = "Zarządzanie kartami płatniczymi klientów")
public class CardController {

    private final CreateCardUseCase createCardUseCase;
    private final ActivateCardUseCase activateCardUseCase;

    public CardController(CreateCardUseCase createCardUseCase, ActivateCardUseCase activateCardUseCase) {
        this.createCardUseCase = createCardUseCase;
        this.activateCardUseCase = activateCardUseCase;
    }

    @PostMapping
    @Operation(summary = "Wygeneruj nową kartę", description = "Tworzy nową wirtualną kartę przypisaną do konta i zwraca jej dane wraz z jawnym CVV.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Karta pomyślnie wygenerowana"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji danych (np. pusty PIN)"),
            @ApiResponse(responseCode = "409", description = "Karta o takim numerze już istnieje (konflikt)")
    })
    public ResponseEntity<CardResponse> createCard(
            @Valid @RequestBody CreateCardRequest request
    ) {
        CreateCardResult result = createCardUseCase.execute(request.accountId(), request.pin());

        CardResponse response = CardResponse.fromDomain(result.card(), result.rawCvv());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Aktywuj kartę", description = "Zmienia status karty z CREATED na ACTIVE, umożliwiając wykonywanie nią płatności.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Karta pomyślnie aktywowana"),
            @ApiResponse(responseCode = "409", description = "Karta jest już aktywna lub ma nieprawidłowy status")
    })
    public ResponseEntity<Void> activateCard(@PathVariable UUID id) {
        activateCardUseCase.activate(id);

        return ResponseEntity.noContent().build();
    }
}