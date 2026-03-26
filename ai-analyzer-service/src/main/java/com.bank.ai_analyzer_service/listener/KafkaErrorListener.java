package com.bank.ai_analyzer_service.listener;

import com.bank.ai_analyzer_service.dto.ErrorLogEvent;
import com.bank.ai_analyzer_service.infrastructure.AiAdvisorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class KafkaErrorListener {

    private final AiAdvisorService aiAdvisorService;

    public KafkaErrorListener(AiAdvisorService aiAdvisorService) {
        this.aiAdvisorService = aiAdvisorService;
    }

    @KafkaListener(topics = "error-logs-topic", groupId = "ai-analyzer-group")
    public void consumeErrorLog(ErrorLogEvent event) {
        log.info("🔥 Otrzymano nowy błąd z Kafki! Serwis: {}", event.serviceName());

        try {
            String aiAdvice = aiAdvisorService.analyzeError(event);
            log.info("Werdykt Sztucznej Inteligencji:\n{}", aiAdvice);
        } catch (Exception e) {
            log.error("Nie udało się przeanalizować błędu przez AI", e);
        }

    }

}
