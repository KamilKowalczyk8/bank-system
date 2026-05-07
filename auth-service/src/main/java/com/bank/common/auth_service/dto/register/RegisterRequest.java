package com.bank.common.auth_service.dto.register;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Schema(description = "Dane wysyłane z formularza do rejestracji konta")
public record RegisterRequest(

        @NotBlank(message = "Email jest wymagany")
        @Email(message = "Niepoprawny format adresu email")
        String email,

        @NotBlank(message = "Numer telefonu jest wymagany")
        @Pattern(regexp = "\\d{9}", message = "Numer telefonu musi składać się z 9 cyfr")
        String phoneNumber

) {}
