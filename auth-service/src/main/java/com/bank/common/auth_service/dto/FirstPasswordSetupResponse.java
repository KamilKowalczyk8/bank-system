package com.bank.common.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Odpowiedź po pomyślnym ustawieniu hasła (wydanie pełnych kluczy)")
public record FirstPasswordSetupResponse(
        @Schema(description = "Komunikat o sukcesie")
        String message,

        @Schema(description = "Pełnoprawny token dostępu do systemu bankowego")
        String accessToken,

        @Schema(description = "Długo żyjący token odświeżania")
        String refreshToken
) {
}
