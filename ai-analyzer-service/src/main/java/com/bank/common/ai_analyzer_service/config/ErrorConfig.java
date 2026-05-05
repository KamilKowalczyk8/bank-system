package com.bank.common.ai_analyzer_service.config;

import com.bank.common.api.ErrorReporter;
import com.bank.common.messaging.ErrorEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ErrorConfig {

    @Value("${spring.application.name:ai-anaylzer-service}")
    private String serviceName;

    @Bean
    public ErrorReporter errorReporter(KafkaTemplate<String, Object> kafkaTemplate) {
        return new ErrorEventPublisher(kafkaTemplate, serviceName);
    }
}

