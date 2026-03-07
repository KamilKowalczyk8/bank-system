package com.bank.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Odpowiedź po udanej weryfikacji hasła")
public record LoginStep2Response(
        @Schema(description = "Zwracamy login (do użycia w kolejnym kroku)", example = "12345678")
        String loginToken,

        @Schema(description = "Instrukcja dla frontendu, co robić dalej", example = "PROVIDE_SMS_CODE")
        String nextStep,

        @Schema(description = "Komunikat dla użytkownika", example = "Wprowadź kod z wiadomości SMS")
        String message
) {}
