package com.bank.common.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Odpowiedź po udanym logowaniu")
public record LoginStep3Response(
        @Schema(description = "Komunikat sukcesu", example = "Logowanie zakończone pomyślnie")
        String message,

        @Schema(description = "Krótko żyjący token dostępu (np. 5 minut)")
        String accessToken,

        @Schema(description = "Długo żyjący token do odświeżania sesji (np. 24h)")
        String refreshToken
) {
}
