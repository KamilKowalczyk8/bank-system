package com.bank.common.notification_service.listener;

import com.bank.common.api.ErrorReporter;
import com.bank.common.notification_service.event.CardCreatedEvent;
import com.bank.common.notification_service.event.CustomerRegisteredEvent;
import com.bank.common.notification_service.event.PaymentFailedEvent;
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
    private final ErrorReporter errorReporter;

    @KafkaListener(topics = "customer-registration-events", groupId = "notification-group")
    public void handleCustomerRegistration(CustomerRegisteredEvent event) {
        log.info("KAFKA = Otrzymano nową paczkę z taśmociągu!");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@naszbank.pl");
            message.setTo(event.email());
            message.setSubject("Witamy w naszym banku");
            message.setText("Dzień dobry!\n\nTwoje konto zostało utworzone. Twoje ID: " + event.authId());

            mailSender.send(message);
            log.info("✅ E-mail wysłany pomyślnie na adres: {}", event.email());

        } catch (Exception e) {
            String msg = "Awaria bramki SMTP podczas wysyłania maila powitalnego do: " + event.email();
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
        }
    }

    @KafkaListener(topics = "card-created-events", groupId = "notification-group")
    public void handleCardCreation(CardCreatedEvent event) {
        log.info("KAFKA = Otrzymano nową paczkę z taśmociągu (Nowa Karta)!");

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
            String msg = "Awaria bramki SMTP podczas wysyłania maila powitalnego do: " + event.email();
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
        }
    }

    @KafkaListener(topics = "payment.failed", groupId = "notification-group")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("KAFKA = Otrzymano zdarzenie o odrzuconym przelewie: {}", event.paymentId());

        try {
            String customerEmail = "adres-do-pobrania@naszbank.pl"; // email narazie na sztywno

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@naszbank.pl");
            message.setTo(customerEmail);

            if ("REJECTED_FRAUD".equals(event.failureReason())) {
                message.setSubject("PILNE: Zablokowana transakcja - podejrzenie oszustwa");
                message.setText("Dzień dobry,\n\nTwoja próba przelewu na kwotę " + event.amount() + " " + event.currency() +
                        " została zablokowana z powodu podejrzenia oszustwa. Skontaktuj się z bankiem natychmiast!");
            } else {
                message.setSubject("Odrzucony przelew");
                message.setText("Dzień dobry,\n\nTwój przelew na kwotę " + event.amount() + " " + event.currency() +
                        " został odrzucony. Powód: Brak wystarczających środków lub inny błąd systemowy.");
            }

            mailSender.send(message);
            log.info("✅ E-mail o odrzuceniu przelewu wysłany pomyślnie na adres: {}", customerEmail);

        } catch (Exception e) {
            String msg = "Awaria bramki SMTP podczas wysyłania powiadomienia o odrzuconym przelewie (PaymentID: " + event.paymentId() + ")";
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
        }
    }

}
