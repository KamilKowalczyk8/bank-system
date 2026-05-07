package com.bank.common.auth_service.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Odpowiedź po weryfikacji loginu")
public record LoginStep1Response(
        @Schema(description = "Zwracany login (do użycia w kolejnym kroku)", example = "15829304")
        String login,

        @Schema(description = "Instrukcja dla frontendu, co robić dalej", example = "PROVIDE_PASSWORD")
        String nextStep,

        @Schema(description = "Komunikat dla użytkownika", example = "Podaj hasło")
        String message
) {}
