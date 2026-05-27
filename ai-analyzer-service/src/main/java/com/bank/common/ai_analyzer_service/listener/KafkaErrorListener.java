package com.bank.common.ai_analyzer_service.listener;

import com.bank.common.ai_analyzer_service.dto.SendNotificationEvent;
import com.bank.common.dto.ErrorLogEvent;
import com.bank.common.ai_analyzer_service.infrastructure.AiAdvisorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.bank.common.api.ErrorReporter;

import java.util.List;

@Slf4j
@Component
public class KafkaErrorListener {

    private final AiAdvisorService aiAdvisorService;
    private final ErrorReporter errorReporter;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaErrorListener(AiAdvisorService aiAdvisorService,
                              ErrorReporter errorReporter,
                              KafkaTemplate<String, Object> kafkaTemplate
    ) {
        this.aiAdvisorService = aiAdvisorService;
        this.errorReporter = errorReporter;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "error-logs-topic", groupId = "ai-analyzer-group")
    public void consumeErrorLog(ErrorLogEvent event) {
        if ("ai-analyzer-service".equals(event.serviceName())) {
            log.warn("Zignorowano błąd własny (AI Analyzer), aby zapobiec pętli nasłuchu.");
            return;
        }

        log.info("Otrzymano nowy błąd z Kafki! Serwis: {}", event.serviceName());

        try {
            String aiAdvice = aiAdvisorService.analyzeError(event);
            log.info("Analiza Gemini wygenerowana pomyślnie.");

            String alertMessage = String.format(
                    "-------*ALERT SYSTEMOWY MIKROSERWISÓW* -------\n" +
                            "*Serwis generujący błąd:* `%s`\n" +
                            "*Komunikat błędu:* `%s`\n\n" +
                            "-------*Rozwiązanie i analiza od Gemini (AI):*\n%s",
                    event.serviceName(), event.message(), aiAdvice
            );

            SendNotificationEvent notification = new SendNotificationEvent(
                    "ALERT SYSTEMOWY MIKROSERWISÓW",
                    alertMessage,
                    List.of("SLACK", "DISCORD")
            );

            log.info("📤 Publikuję SendNotificationEvent na Kafkę dla notification-service...");
            kafkaTemplate.send("system-notifications-topic", notification);

        } catch (Exception e) {
            String msg = "Awaria modułu AI podczas analizy błędu z serwisu: " + event.serviceName();
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
        }
    }
}
