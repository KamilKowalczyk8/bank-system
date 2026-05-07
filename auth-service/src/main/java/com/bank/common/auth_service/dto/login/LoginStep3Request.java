package com.bank.common.auth_service.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Żądanie weryfikacji kodu SMS (Krok 3 logowania)")
public record LoginStep3Request(
        @Schema(description = "Identyfikator sesji otrzymany w Kroku 2")
        @NotNull(message = "Session ID nie może być puste")
        UUID sessionId,

        @Schema(description = "6-cyfrowy kod z wiadomości SMS", example = "492811")
        @NotBlank(message = "Kod SMS nie może być pusty")
        String smsCode
) {}


