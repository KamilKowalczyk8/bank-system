package com.bank.common.ai_analyzer_service.dto;

import java.util.List;

public record SendNotificationEvent(
        String title,
        String content,
        List<String> channels //Slack / Discord
) {}
