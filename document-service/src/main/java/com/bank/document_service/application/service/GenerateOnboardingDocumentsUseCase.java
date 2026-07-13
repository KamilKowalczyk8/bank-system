package com.bank.document_service.application.service;

import com.bank.document_service.application.port.DocumentEventPublisher;
import com.bank.document_service.application.port.DocumentStoragePort;
import com.bank.document_service.application.port.PasswordGeneratorPort;
import com.bank.document_service.application.port.PdfGeneratorPort;
import com.bank.document_service.domain.Document;
import com.bank.document_service.domain.DocumentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class GenerateOnboardingDocumentsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GenerateOnboardingDocumentsUseCase.class);
    private final DocumentEventPublisher documentEventPublisher;
    private final DocumentStoragePort documentStoragePort;
    private final PasswordGeneratorPort passwordGeneratorPort;
    private final PdfGeneratorPort pdfGeneratorPort;

    public GenerateOnboardingDocumentsUseCase(
            DocumentEventPublisher documentEventPublisher,
            DocumentStoragePort documentStoragePort,
            PasswordGeneratorPort passwordGeneratorPort,
            PdfGeneratorPort pdfGeneratorPort
    ) {
        this.documentEventPublisher = documentEventPublisher;
        this.documentStoragePort = documentStoragePort;
        this.passwordGeneratorPort = passwordGeneratorPort;
        this.pdfGeneratorPort = pdfGeneratorPort;
    }

    public void execute(UUID userId, String firstName, String lastName, String login, String customerEmail, String phoneNumber, String bankTemporaryPassword) {
        log.info("DEBUG: 1. Inicjalizacja encji Document");
        Document contract = new Document(userId, DocumentType.ACCOUNT_CONTRACT);

        log.info("DEBUG: 2. Generowanie hasła tymczasowego");
        String documentPassword = passwordGeneratorPort.generateTemporaryPassword();

        log.info("DEBUG: 3. Generowanie pliku PDF przez adapter");
        byte[] contractPdf = pdfGeneratorPort.generateContract(userId, firstName, lastName, login, bankTemporaryPassword, documentPassword);

        log.info("DEBUG: 4. Zapis pliku na dysk");
        String contractPath = documentStoragePort.saveDocument("umowa_" + userId + ".pdf", contractPdf);

        log.info("DEBUG: 5. Publikacja zdarzenia z powrotem na Kafkę");
        contract.markAsGenerated(contractPath);

        documentEventPublisher.publishDocumentsReadyEvent(
                customerEmail,
                phoneNumber,
                contractPath,
                documentPassword,
                login,
                bankTemporaryPassword
        );
        log.info("DEBUG: 6. Koniec Use Case");
    }
}
