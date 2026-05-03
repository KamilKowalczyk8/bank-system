package com.bank.common.onboarding_service.config;

import com.bank.common.api.ErrorReporter;
import com.bank.common.messaging.ErrorEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ErrorConfig {

    @Value("${spring.application.name:api-gateway}")
    private String serviceName;

    @Bean
    public ErrorReporter errorReporter(KafkaTemplate<String, Object> kafkaTemplate) {
        return new ErrorEventPublisher(kafkaTemplate, serviceName);
    }
}

