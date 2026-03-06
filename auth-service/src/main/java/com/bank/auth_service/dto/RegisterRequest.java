package com.bank.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dane wysyłane z formularza do rejestracji konta")
public record RegisterRequest(

        @NotBlank(message = "Imię jest wymagane")
        @Size(min = 2, message = "Imię musi mieć min. 2 znaki")
        String firstName,

        @NotBlank(message = "Nazwisko jest wymagane")
        @Size(min = 2, message = "Nazwisko musi mieć min. 2 znaki")
        String lastName,

        @NotBlank(message = "PESEL jest wymagany")
        @Size(min = 11, max = 11, message = "PESEL musi mieć 11 cyfr")
        String PESEL,

        @NotBlank(message = "Numer telefonu jest wymagany")
        @Pattern(regexp = "\\d{9}", message = "Numer telefonu musi składać się z 9 cyfr")
        String phoneNumber

) {}
