package com.bank.common.ai_analyzer_service.listener;

import com.bank.common.ai_analyzer_service.application.port.NotificationPort;
import com.bank.common.dto.ErrorLogEvent;
import com.bank.common.ai_analyzer_service.infrastructure.AiAdvisorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.bank.common.api.ErrorReporter;

import java.util.List;

@Slf4j
@Component
public class KafkaErrorListener {

    private final AiAdvisorService aiAdvisorService;
    private final ErrorReporter errorReporter;
    private final List<NotificationPort> notificationChannels;

    public KafkaErrorListener(AiAdvisorService aiAdvisorService, ErrorReporter errorReporter, List<NotificationPort> notificationChannels) {
        this.aiAdvisorService = aiAdvisorService;
        this.errorReporter = errorReporter;
        this.notificationChannels = notificationChannels;
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

            notificationChannels.forEach(channel -> channel.sendNotification(alertMessage));

        } catch (Exception e) {
            String msg = "Awaria modułu AI podczas analizy błędu z serwisu: " + event.serviceName();
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
        }

    }

}
