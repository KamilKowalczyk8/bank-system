package com.bank.document_service.application.service;

import com.bank.document_service.application.port.DocumentEventPublisher;
import com.bank.document_service.application.port.DocumentStoragePort;
import com.bank.document_service.application.port.PasswordGeneratorPort;
import com.bank.document_service.application.port.PdfGeneratorPort;
import com.bank.document_service.domain.Document;
import com.bank.document_service.domain.DocumentType;

import java.util.UUID;

public class GenerateOnboardingDocumentsUseCase {

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
        Document contract = new Document(userId, DocumentType.ACCOUNT_CONTRACT);

        String documentPassword = passwordGeneratorPort.generateTemporaryPassword();

        byte[] contractPdf = pdfGeneratorPort.generateContract(userId, firstName, lastName, login, bankTemporaryPassword, documentPassword);

        String contractPath = documentStoragePort.saveDocument("umowa_" + userId + ".pdf", contractPdf);

        contract.markAsGenerated(contractPath);

        documentEventPublisher.publishDocumentsReadyEvent(
                customerEmail,
                phoneNumber,
                contractPath,
                documentPassword,
                login,
                bankTemporaryPassword
        );
    }
}
