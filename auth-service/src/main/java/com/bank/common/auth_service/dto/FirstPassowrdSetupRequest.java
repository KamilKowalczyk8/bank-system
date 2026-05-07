package com.bank.common.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Żądanie ustawienia pierwszego, stałego hasła przez użytkownika")
public record FirstPassowrdSetupRequest(
        @Schema(description = "Nowe, silne hasło", example = "SilneHaslo123!")
        @NotBlank(message = "Nowe hasło nie może być puste")
        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[\\W_]).{15,}$",
                message = "Hasło musi mieć min. 15 znaków, zawierać dużą i małą literę, cyfrę oraz znak specjalny"
        )
        String newPassword,

        @Schema(description = "Potwierdzenie nowego hasła", example = "SilneHaslo123!")
        @NotBlank(message = "Potwierdzenie hasła jest wymagane")
        String confirmPassword
) {
}
