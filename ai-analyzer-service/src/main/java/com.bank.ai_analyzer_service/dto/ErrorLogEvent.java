package com.bank.ai_analyzer_service.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorLogEvent(
    String serviceName,
    String errorMessage,
    String stackTrace,
    LocalDateTime timestamp
) { }
