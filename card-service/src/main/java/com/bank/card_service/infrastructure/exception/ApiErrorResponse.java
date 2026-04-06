package com.bank.card_service.infrastructure.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
        String message,
        List<String> details,
        LocalDateTime timestamp
) {
}
