package com.bank.notification_service.event;

public record CustomerRegisteredEvent(
        String authId,
        String email,
        String pesel
) {
}
