package com.bank.common.api_gateway.dto;

import java.time.Instant;

public record GatewayErrorResponse(
        String timestamp,
        int status,
        String error,
        String message,
        String path
) {
    public static GatewayErrorResponse of(int status, String error, String message, String path) {
        return new GatewayErrorResponse(
                Instant.now().toString(),
                status,
                error,
                message,
                path
        );
    }
}
