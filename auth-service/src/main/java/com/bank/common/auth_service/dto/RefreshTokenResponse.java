package com.bank.common.auth_service.dto;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
