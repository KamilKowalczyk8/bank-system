package com.bank.common.ai_analyzer_service.infrastructure.slack;

import com.bank.common.ai_analyzer_service.application.port.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestClientSsl;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.Map;

@Slf4j
@Component
public class SlackNotificationAdapter implements NotificationPort {

    private final RestClient restClient;
    private final String webhookUrl;

    public SlackNotificationAdapter(RestClient.Builder restClientBuilder,
                                    @Value("${integrations.slack.webhook-url}") String webhookUrl) {
        this.restClient = restClientBuilder.build();
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendNotification(String message) {
        if (webhookUrl == null || webhookUrl.isBlank() || webhookUrl.startsWith("${")) {
            log.warn("Slack Webhook URL nie jest skonfigurowany w zmiennych. Cofam wysyłkę.");
            return;
        }

        try {
            log.info("Wysyłam powiadomienie AI na Slacka");
            restClient.post()
                    .uri(webhookUrl)
                    .body(Map.of("text", message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Błąd podczas wysyłania na Slacka: {}", e.getMessage());
        }
    }
}
