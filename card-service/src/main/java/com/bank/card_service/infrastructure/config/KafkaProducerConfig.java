package com.bank.card_service.infrastructure.config;

import com.bank.card_service.infrastructure.dto.event.CardCreatedEvent;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.bank.common.dto.ErrorLogEvent;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    private Map<String, Object> baseConfig() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                org.springframework.kafka.support.serializer.JsonSerializer.class);

        return config;
    }

    @Bean
    public ProducerFactory<String, CardCreatedEvent> cardProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfig());
    }

    @Bean("cardKafkaTemplate")
    public KafkaTemplate<String, CardCreatedEvent> cardKafkaTemplate() {
        return new KafkaTemplate<>(cardProducerFactory());
    }

    @Bean
    public ProducerFactory<String, ErrorLogEvent> errorProducerFactory() {
        return new DefaultKafkaProducerFactory<>(baseConfig());
    }

    @Bean("errorKafkaTemplate")
    public KafkaTemplate<String, ErrorLogEvent> errorKafkaTemplate() {
        return new KafkaTemplate<>(errorProducerFactory());
    }
}