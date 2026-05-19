package com.bank.document_service.application.port;

public interface DocumentStoragePort {
    String saveDocument(String fileName, byte[] content);
}
