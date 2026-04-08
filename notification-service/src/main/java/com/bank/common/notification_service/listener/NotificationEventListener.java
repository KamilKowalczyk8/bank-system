package com.bank.common.notification_service.listener;

import com.bank.common.notification_service.event.CardCreatedEvent;
import com.bank.common.notification_service.event.CustomerRegisteredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class NotificationEventListener {

    private final JavaMailSender mailSender;

    @KafkaListener(topics = "customer-registration-events", groupId = "notification-group")
    public void handleCustomerRegistration(CustomerRegisteredEvent event) {
        log.info("[KAFKA] Otrzymano nową paczkę z taśmociągu!");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@naszbank.pl");
            message.setTo(event.email());
            message.setSubject("Witamy w naszym banku");
            message.setText("Dzień dobry!\n\nTwoje konto zostało utworzone. Twoje ID: " + event.authId());

            mailSender.send(message);
            log.info("✅ E-mail wysłany pomyślnie na adres: {}", event.email());

        } catch (Exception e) {
            log.error("Błąd podczas wysyłki e-maila: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = "card-created-events", groupId = "notification-group")
    public void handleCardCreation(CardCreatedEvent event) {
        log.info("[KAFKA] Otrzymano nową paczkę z taśmociągu (Nowa Karta)!");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@naszbank.pl");
            message.setTo(event.email());
            message.setSubject("Twoja nowa karta wirtualna");
            message.setText("Dzień dobry!\n\n" +
                    "Informujemy, że Twoja nowa karta z końcówką " + event.cardNumber() + " została wygenerowana i czeka na aktywację.\n\n" +
                    "Pozdrawiamy,\nZespół Banku");

            mailSender.send(message);
            log.info("✅ E-mail o nowej karcie wysłany pomyślnie na adres: {}", event.email());
        } catch (Exception e) {
            log.error("Błąd podczas wysyłki e-maila o nowej karcie: {}", e.getMessage());
        }
    }

}
