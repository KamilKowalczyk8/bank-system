package com.bank.common.notification_service.infrastructure.discord;

import com.bank.common.notification_service.port.NotificationPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Component
public class DiscordNotificationAdapter implements NotificationPort {

    private final RestClient restClient;
    private final String webhookUrl;

    public DiscordNotificationAdapter(RestClient.Builder restClientBuilder,
                                      @Value("${integrations.discord.webhook-url}") String webhookUrl) {
        this.restClient = restClientBuilder.build();
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void sendNotification(String message) {
        if (webhookUrl == null || webhookUrl.isBlank() || webhookUrl.startsWith("${")) {
            log.warn("Discord Webhook URL nie jest skonfigurowany w zmiennych. Cofam wysyłkę.");
            return;
        }

        try {
            log.info("Wysyłam powiadomienie AI do archiwum na Discordzie");
            restClient.post()
                    .uri(webhookUrl)
                    .body(Map.of("content", message))
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Błąd podczas wysyłania na Discorda: {}", e.getMessage());
        }
    }

    @Override
    public String getChannelType() {
        return "DISCORD";
    }
}
