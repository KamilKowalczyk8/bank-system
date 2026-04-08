package com.bank.common.notification_service.event;

import java.time.LocalDateTime;
import java.util.UUID;

public record CardCreatedEvent(
        UUID cardId,
        UUID accountId,
        String cardNumber,
        String email,
        LocalDateTime createdAt
) {}
