package com.bank.common.auth_service.config;
import com.bank.common.dto.ErrorLogEvent;
import com.bank.common.api.ErrorReporter;
import com.bank.common.messaging.ErrorEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
public class ErrorConfig {

    @Bean
    public ErrorReporter errorReporter(
            KafkaTemplate<String, ErrorLogEvent> kafkaTemplate,
            @Value("${spring.application.name:auth-service}") String serviceName) {

        return new ErrorEventPublisher(kafkaTemplate, serviceName);
    }
}