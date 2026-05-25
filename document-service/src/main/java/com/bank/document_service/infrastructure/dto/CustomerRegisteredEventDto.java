package com.bank.document_service.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CustomerRegisteredEventDto(
        String authId,
        String email,
        String phoneNumber,
        String firstName,
        String lastName,
        String login,
        @JsonProperty("temporaryPassword")
        String bankTemporaryPassword
) {
}
