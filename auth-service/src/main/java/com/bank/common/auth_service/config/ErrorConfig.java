package com.bank.common.auth_service.config;

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
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ErrorConfig {

    @Value("${spring.application.name:auth-service}")
    private String serviceName;

    @Value("${spring.kafka.bootstrap-servers:kafka:9092}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, ErrorLogEvent> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, ErrorLogEvent> errorLogKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ErrorReporter errorReporter(KafkaTemplate<String, ErrorLogEvent> errorLogKafkaTemplate) {
        return new ErrorEventPublisher(errorLogKafkaTemplate, serviceName);
    }
}