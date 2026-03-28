package com.bank.common.auth_service.dto;

public record LogoutRequest(
        String refreshToken
) {}
