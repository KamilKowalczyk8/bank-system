package com.bank.document_service.infrastructure.messaging;

import com.bank.document_service.application.service.GenerateOnboardingDocumentsUseCase;
import com.bank.document_service.infrastructure.dto.CustomerRegisteredEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class DocumentKafkaListener {

    private static final Logger log = LoggerFactory.getLogger(DocumentKafkaListener.class);

    private final GenerateOnboardingDocumentsUseCase generateOnboardingDocumentsUseCase;

    public DocumentKafkaListener(GenerateOnboardingDocumentsUseCase generateOnboardingDocumentsUseCase) {
        this.generateOnboardingDocumentsUseCase = generateOnboardingDocumentsUseCase;
    }

    @KafkaListener(topics = "customer-registration-events", groupId = "document-service-group")
    public void consumeCustomerRegisteredEvent(CustomerRegisteredEventDto eventDto) {
        log.info("Otrzymano zdarzenie z Onboarding Service dla klienta: {}", eventDto.login());

        try {
            generateOnboardingDocumentsUseCase.execute(
                    java.util.UUID.fromString(eventDto.authId()),
                    eventDto.firstName(),
                    eventDto.lastName(),
                    eventDto.login(),
                    eventDto.email(),
                    eventDto.phoneNumber(),
                    eventDto.bankTemporaryPassword()
            );

            log.info("Sukces! Proces generowania dokumentów dla {} zakończony.", eventDto.login());
        } catch (Throwable e) {
            log.error("Krytyczny błąd/Error podczas generowania dokumentów z Kafki: {}", e.getMessage(), e);
        }
    }
}
