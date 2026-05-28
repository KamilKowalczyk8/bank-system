package com.bank.audit_service.dto;

import java.util.List;

public record SendNotificationEvent(
        String title,
        String content,
        List<String> channels
) {}
