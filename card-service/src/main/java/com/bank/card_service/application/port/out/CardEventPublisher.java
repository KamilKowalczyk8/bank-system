package com.bank.card_service.application.port.out;

import com.bank.card_service.infrastructure.dto.event.CardCreatedEvent;

public interface CardEventPublisher {
    void publishCardCreated(CardCreatedEvent event);
}
