package com.bank.card_service.infrastructure.config;

import com.bank.common.api.ErrorReporter;
import com.bank.common.messaging.ErrorEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bank.common.dto.ErrorLogEvent;

import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ErrorConfig {

    @Value("${spring.application.name:api-gateway}")
    private String serviceName;

    @Bean
    public ErrorReporter errorReporter(
            @Qualifier("errorKafkaTemplate")
            KafkaTemplate<String, ErrorLogEvent> errorKafkaTemplate) {

        return new ErrorEventPublisher(errorKafkaTemplate, serviceName);
    }
}

