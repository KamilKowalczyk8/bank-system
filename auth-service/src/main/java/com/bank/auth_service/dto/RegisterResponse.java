package com.bank.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dane wysyłane do frontendu po pomyślnej rejestracji")
public record RegisterResponse(
        String login,
        String message
) {}
