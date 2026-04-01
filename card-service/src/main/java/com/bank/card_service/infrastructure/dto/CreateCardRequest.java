package com.bank.card_service.infrastructure.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record CreateCardRequest(

        @NotNull(message = "ID konta (accountId) jest wymagane.")
        UUID accountId,

        @NotBlank(message = "PIN nie może byc pusty")
        @Pattern(regexp = "^\\d{4}", message = "PIN musi składać się dokładnie z 4 cyfr.")
        String pin
) {
}
