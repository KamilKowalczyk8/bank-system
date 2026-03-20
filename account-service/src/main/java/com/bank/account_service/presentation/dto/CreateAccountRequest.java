package com.bank.account_service.presentation.dto;

import com.bank.account_service.domain.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateAccountRequest(

        @NotBlank(message = "Customer ID nie może być puste")
        @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "Niedozwolone znaki w Customer ID")
        String customerId,

        @NotNull(message = "Waluta jest wymagana")
        Currency currency
) {
}
