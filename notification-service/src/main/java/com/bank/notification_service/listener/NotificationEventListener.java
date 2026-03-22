package com.bank.notification_service.listener;

import com.bank.notification_service.event.CustomerRegisteredEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationEventListener {

    @KafkaListener(topics = "customer-registration-events", groupId = "notification-group")
    public void handleCustomerRegistration(CustomerRegisteredEvent event) {
        log.info("🔔 [KAFKA] Otrzymano nową paczkę z taśmociągu!");
        log.info("Rozpoczynam wysyłkę e-maila powitalnego na adres: {}", event.email());

        log.info("Drogi Kliencie (PESEL: {}), witamy w naszym banku! Twoje ID to: {}", event.pesel(), event.authId());
        log.info("✅ E-mail wysłany pomyślnie.\n");
    }

}
