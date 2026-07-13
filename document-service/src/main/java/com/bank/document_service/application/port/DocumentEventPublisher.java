package com.bank.document_service.application.port;

public interface DocumentEventPublisher {
    void publishDocumentsReadyEvent(String customerEmail, String phoneNumber, String contractPath, String documentPassword, String login, String bankTemporaryPassword);
}
