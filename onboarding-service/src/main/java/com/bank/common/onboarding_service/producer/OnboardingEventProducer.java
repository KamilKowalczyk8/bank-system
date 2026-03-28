package com.bank.common.onboarding_service.producer;

import com.bank.onboarding_service.event.CustomerRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private static final String TOPIC = "customer-registration-events";

    public void sendCsutomerRegisteredEvent(CustomerRegisteredEvent event) {
        log.info("Wysyłam zdarzenie rejestracji do Kafki (Temat: {}): {}", TOPIC, event);

        kafkaTemplate.send(TOPIC, event.authId(), event);
    }
}
