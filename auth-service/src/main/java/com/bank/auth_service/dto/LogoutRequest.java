package com.bank.auth_service.dto;

public record LogoutRequest(
        String refreshToken
) {}
