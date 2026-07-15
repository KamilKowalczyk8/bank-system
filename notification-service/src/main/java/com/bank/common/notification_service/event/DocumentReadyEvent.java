package com.bank.common.notification_service.event;

public record DocumentReadyEvent(
        String customerEmail,
        String phoneNumber,
        String contractPath,
        String documentPassword,
        String login,
        String bankTemporaryPassword

) {
}
