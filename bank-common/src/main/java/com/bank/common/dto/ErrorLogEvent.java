package com.bank.common.dto;

import java.time.LocalDateTime;

public record ErrorLogEvent(
        String version,
        String serviceName,
        String level,
        String message,
        String stackTrace,
        LocalDateTime timestamp
) {
}
