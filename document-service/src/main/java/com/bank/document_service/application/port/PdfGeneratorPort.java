package com.bank.document_service.application.port;

import java.util.UUID;

public interface PdfGeneratorPort {
    byte[] generateContract(UUID userId, String firstName, String lastName, String login, String bankTemporaryPassword, String documentPassword);
}
