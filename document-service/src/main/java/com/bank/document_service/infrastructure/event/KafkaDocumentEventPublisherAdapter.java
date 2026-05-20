package com.bank.document_service.infrastructure.event;

import com.bank.document_service.application.port.DocumentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaDocumentEventPublisherAdapter implements DocumentEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(KafkaDocumentEventPublisherAdapter.class);

    @Override
    public void publishDocumentsReadyEvent(String customerEmail, String phoneNumber, String contractPath, String documentPassword) {
    //TODO tymczasowe rozwiązanie w postacji logów w przyszłosci będzie to rozwinięte od dalszego etapu juz  prawdziwego wysłamnia

        log.info("Publikacja zdarzenia o gotowych dokumentach!");
        log.info("Email klienta: {}", customerEmail);
        log.info("Numer telefonu: {}", phoneNumber);
        log.info("Ścieżka do załącznika: {}", contractPath);
        log.info("Hasło do otwarcia PDF: {}", documentPassword);
    }
}
