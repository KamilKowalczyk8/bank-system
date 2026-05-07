package com.bank.common.auth_service.dto.token;

public record RefreshTokenResponse(
        String accessToken,
        String refreshToken
) {}
