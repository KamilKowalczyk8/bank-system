package com.bank.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Odpowiedź po udanej weryfikacji hasła")
public record LoginStep2Response(
        @Schema(description = "Unikalny identyfikator sesji logowania", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID sessionId,

        @Schema(description = "Instrukcja dla frontendu, co robić dalej", example = "PROVIDE_SMS_CODE")
        String nextStep,

        @Schema(description = "Komunikat dla użytkownika", example = "Wprowadź kod z wiadomości SMS")
        String message
) {}
