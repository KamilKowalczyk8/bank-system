package com.bank.common.onboarding_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        String userId,
        String login,
        String temporaryPassword,
        String message
) {}
