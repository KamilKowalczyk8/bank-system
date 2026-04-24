package com.bank.common.ai_analyzer_service.listener;

import com.bank.common.ai_analyzer_service.dto.ErrorLogEvent;
import com.bank.common.ai_analyzer_service.infrastructure.AiAdvisorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.bank.common.api.ErrorReporter;

@Slf4j
@Component
public class KafkaErrorListener {

    private final AiAdvisorService aiAdvisorService;
    private final ErrorReporter errorReporter;

    public KafkaErrorListener(AiAdvisorService aiAdvisorService, ErrorReporter errorReporter) {
        this.aiAdvisorService = aiAdvisorService;
        this.errorReporter = errorReporter;
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
            log.info("Werdykt Sztucznej Inteligencji:\n{}", aiAdvice);
        } catch (Exception e) {
            String msg = "Awaria AI podczas analizy błędu z serwisu: " + event.serviceName();
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
        }

    }

}
