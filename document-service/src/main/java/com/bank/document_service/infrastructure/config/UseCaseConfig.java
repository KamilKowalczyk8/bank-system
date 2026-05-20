package com.bank.document_service.infrastructure.config;

import com.bank.document_service.application.port.DocumentEventPublisher;
import com.bank.document_service.application.port.DocumentStoragePort;
import com.bank.document_service.application.port.PasswordGeneratorPort;
import com.bank.document_service.application.port.PdfGeneratorPort;
import com.bank.document_service.application.service.GenerateOnboardingDocumentsUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public GenerateOnboardingDocumentsUseCase generateOnboardingDocumentsUseCase(
            DocumentEventPublisher documentEventPublisher,
            DocumentStoragePort documentStoragePort,
            PasswordGeneratorPort passwordGeneratorPort,
            PdfGeneratorPort pdfGeneratorPort
    ) {
        return new GenerateOnboardingDocumentsUseCase(
                documentEventPublisher,
                documentStoragePort,
                passwordGeneratorPort,
                pdfGeneratorPort
        );
    }
}
