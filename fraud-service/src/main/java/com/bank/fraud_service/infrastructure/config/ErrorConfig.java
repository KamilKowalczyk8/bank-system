package com.bank.fraud_service.infrastructure.config;

import com.bank.common.api.ErrorReporter;
import com.bank.common.dto.ErrorLogEvent;
import com.bank.common.messaging.ErrorEventPublisher;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ErrorConfig {

    @Value("${spring.application.name:api-gateway}")
    private String serviceName;

    @Bean
    public ErrorReporter errorReporter(KafkaTemplate<String, Object> kafkaTemplate) {
        return new ErrorEventPublisher(kafkaTemplate, serviceName);
    }
}


