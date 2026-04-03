package com.bank.common.onboarding_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponse(
        @JsonProperty("login")
        String authId,

        String message
) {}
