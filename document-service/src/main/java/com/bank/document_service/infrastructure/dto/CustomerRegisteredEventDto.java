package com.bank.document_service.infrastructure.dto;

import java.util.UUID;

public record CustomerRegisteredEventDto(
        UUID userId,
        String customerEmail,
        String phoneNumber,
        String firstName,
        String lastName,
        String login,
        String bankTemporaryPassword
) {
}
