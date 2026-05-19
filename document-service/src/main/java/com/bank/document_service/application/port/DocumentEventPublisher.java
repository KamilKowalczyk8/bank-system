package com.bank.document_service.application.port;

public interface DocumentEventPublisher {
    void publishDocumentsReadyEvent(
            String customerEmail,
            String contractReference,
            String credentialsReference);
}
