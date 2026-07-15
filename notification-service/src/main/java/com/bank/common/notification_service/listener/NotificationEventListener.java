package com.bank.common.notification_service.listener;

import com.bank.common.api.ErrorReporter;
import com.bank.common.notification_service.event.*;
import com.bank.common.notification_service.port.NotificationPort;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.File;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class NotificationEventListener {

    private final JavaMailSender mailSender;
    private final ErrorReporter errorReporter;
    private final S3Client s3Client;

    private final List<NotificationPort> notificationChannels;

    @KafkaListener(topics = "system-notifications-topic")
    public void handleSystemNotification(SendNotificationEvent event) {
        log.info("KAFKA = Otrzymano żądanie wysyłki powiadomienia systemowego od AI: {}", event.title());

        try {
            String fullMessage = event.title() + "\n\n" + event.content();

            for (NotificationPort channel : notificationChannels) {
                if (event.channels().contains(channel.getChannelType())) {
                    channel.sendNotification(fullMessage);
                }
            }
        } catch (Exception e) {
            String msg = "Awaria podczas routowania powiadomienia systemowego AI: " + event.title();
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
        }
    }

    @KafkaListener(topics = "document-ready-events")
    public void handleDocumentsReady(DocumentReadyEvent event) {
        log.info("KAFKA = Otrzymano gotowe dokumenty. Wysyłam e-mail powitalny do: {}", event.customerEmail());

        try {
            software.amazon.awssdk.services.s3.model.GetObjectRequest getObjectRequest =  //pobranie pliku pdf z MiniO do pamięci RAM
                    software.amazon.awssdk.services.s3.model.GetObjectRequest.builder()
                            .bucket("bank-documents")
                            .key(event.contractPath())
                            .build();

            byte[] fileBytes = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("KowalczykBank@naszbank.pl");
            helper.setTo(event.customerEmail());
            helper.setSubject("Witamy w KowalczykBank! Twoja umowa i dane logowania");

            String emailText = "Dzień dobry!\n\n" +
                    "Twoje konto zostało pomyślnie utworzone. Cieszymy się, że jesteś z nami!\n\n" +
                    "Oto Twoje poufne dane do bankowości elektronicznej:\n" +
                    "Login (Identyfikator): " + event.login() + "\n" +
                    "Hasło startowe: " + event.bankTemporaryPassword() + "\n\n" +
                    "W załączniku przesyłamy umowę o prowadzenie rachunku bankowego. " +
                    "Ze względów bezpieczeństwa plik został zaszyfrowany. \n" +
                    "Twoje hasło do otwarcia pliku PDF to: " + event.documentPassword() + "\n\n" +
                    "Pozdrawiamy,\nZespół KowalczykBank S.A.";

            helper.setText(emailText);

            helper.addAttachment("Umowa_Konta.pdf", new org.springframework.core.io.ByteArrayResource(fileBytes));

            mailSender.send(mimeMessage);
            log.info("✅ Kompletny e-mail powitalny z umową wysłany pomyślnie na adres: {}", event.customerEmail());
        } catch (software.amazon.awssdk.services.s3.model.NoSuchKeyException e) {
            log.error("Awaria pobierania: Plik {} nie istnieje w MinIO!", event.contractPath());
        } catch (Exception e) {
            String msg = "Awaria bramki SMTP / S3 podczas wysyłania dokumentów do: " + event.customerEmail();
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
        }
    }


    @KafkaListener(topics = "card-created-events")
    public void handleCardCreation(CardCreatedEvent event) {
        log.info("KAFKA = Otrzymano nową paczkę z taśmociągu (Nowa Karta)!");

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("KowalczykBank@naszbank.pl");
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

    @KafkaListener(topics = "payment.failed")
    public void handlePaymentFailed(PaymentFailedEvent event) {
        log.info("KAFKA = Otrzymano zdarzenie o odrzuconym przelewie: {}", event.paymentId());

        try {
            String customerEmail = "adres-do-pobrania@naszbank.pl"; // email narazie na sztywno

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("KowalczykBank@naszbank.pl");
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
