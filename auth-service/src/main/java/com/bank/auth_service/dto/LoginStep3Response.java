package com.bank.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Odpowiedź po udanym logowaniu")
public record LoginStep3Response(
        @Schema(description = "Komunikat sukcesu", example = "Logowanie zakończone pomyślnie")
        String message,

        @Schema(description = "Ostateczny token dostępu do banku (JWT)")
        String accessToken

) {
}
