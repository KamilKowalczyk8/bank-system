package com.bank.common.customer_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRegistrationRequest(

        @NotBlank(message = "ID z systemu autoryzacji jest wymagane")
        String authId,

        @NotBlank(message = "Imię jest wymagane")
        String firstName,

        @NotBlank(message = "Nazwisko jest wymagane")
        String lastName,

        @NotBlank(message = "PESEL jest wymagany")
        @Size(min = 11, max = 11, message = "PESEL musi mieć 11 cyfr")
        String pesel,

        @NotBlank(message = "Numer telefonu jest wymagany")
        @Pattern(regexp = "\\d{9}", message = "Numer telefonu musi składać się z 9 cyfr")
        String phoneNumber,

        @NotBlank(message = "Email jest wymagany")
        @Email(message = "Niepoprawny format adresu email")
        String email,

        //Dane adresowe

        @NotBlank(message = "Ulica jest wymagana")
        String street,

        @NotBlank(message = "Numer budynku jest wymagany")
        String buildingNumber,

        String apartmentNumber,

        @NotBlank(message = "Miasto jest wymagane")
        String city,

        @NotBlank(message = "Kod pocztowy jest wymagany")
        String zipCode,

        @NotBlank(message = "Kraj jest wymagany")
        String country

) {}

