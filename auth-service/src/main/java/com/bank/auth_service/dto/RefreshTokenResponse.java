package com.bank.auth_service.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
