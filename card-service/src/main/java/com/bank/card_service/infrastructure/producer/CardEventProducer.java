package com.bank.card_service.infrastructure.producer;

import com.bank.card_service.application.port.out.CardEventPublisher;
import com.bank.card_service.infrastructure.dto.event.CardCreatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CardEventProducer implements CardEventPublisher {

    private static final String TOPIC = "card-created-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public CardEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishCardCreated(CardCreatedEvent event) {
        log.info("Wysyłam zdarzenie o utworzeniu karty dla konta: {}", event.accountId());

        kafkaTemplate.send(TOPIC, event.accountId().toString(), event);
    }

}
