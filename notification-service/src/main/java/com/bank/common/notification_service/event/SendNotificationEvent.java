package com.bank.common.notification_service.event;

import java.util.List;

public record SendNotificationEvent(
        String title,
        String content,
        List<String> channels
) {}
