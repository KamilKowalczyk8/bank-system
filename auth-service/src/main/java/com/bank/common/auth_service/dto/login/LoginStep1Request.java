package com.bank.common.auth_service.dto.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Żądanie weryfikacji loginu (Krok 1 logowania)")
public record LoginStep1Request(
        @Schema(description = "Techniczny login użytkownika (8 cyfr)", example = "15829304")
        @NotBlank(message = "Login nie może być pusty")
        String login
) {}
