package com.bank.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Żądanie weryfikacji hasła (Krok 2 logowania)")
public record LoginStep2Request(
    @Schema(description = "Techniczny login użytkownika (8 cyfr)", example = "12345678")
    @NotBlank(message = "Login nie może być pusty")
    String login,

    @Schema(description = "Hasło użytkownika", example = "ABF351SA")
    @NotBlank(message = "Hasło nie może być puste")
    String password
) {
}
