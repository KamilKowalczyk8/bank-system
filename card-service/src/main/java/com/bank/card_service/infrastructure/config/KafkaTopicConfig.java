package com.bank.card_service.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic cardCreatedTopic() {
        return TopicBuilder.name("card-created-events")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
