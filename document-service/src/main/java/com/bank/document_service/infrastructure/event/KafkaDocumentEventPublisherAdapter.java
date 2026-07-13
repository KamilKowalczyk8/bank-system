package com.bank.document_service.infrastructure.event;

import com.bank.document_service.application.port.DocumentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDocumentEventPublisherAdapter implements DocumentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaDocumentEventPublisherAdapter.class);

    private static final String TOPIC = "document-ready-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDocumentEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishDocumentsReadyEvent(String customerEmail, String phoneNumber, String contractPath, String documentPassword, String login, String bankTemporaryPassword) {
        log.info("Wysyłam zdarzenie na Kafkę dla klienta: {}", customerEmail);

        DocumentReadyEvent payload = new DocumentReadyEvent(
                customerEmail,
                phoneNumber,
                contractPath,
                documentPassword,
                login,
                bankTemporaryPassword
        );

        kafkaTemplate.send(TOPIC, customerEmail, payload);
    }

    private record DocumentReadyEvent(
            String customerEmail,
            String phoneNumber,
            String contractPath,
            String documentPassword,
            String login,
            String bankTemporaryPassword
    ) {}
}
