package com.bank.common.onboarding_service.event;

public record CustomerRegisteredEvent(
        String authId,
        String email,
        String pesel
) {
}
