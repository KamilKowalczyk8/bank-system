package com.bank.document_service.domain;

import java.time.LocalDateTime;
import java.util.UUID;

public class Document {

    private final UUID id;
    private final UUID userId;
    private final DocumentType type;
    private final LocalDateTime createdAt;

    private DocumentStatus status;
    private String storageReference;

    public Document(UUID userId, DocumentType type) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.type = type;
        this.status = DocumentStatus.PENDING;
    }

    public void markAsGenerated(String storageReference) {
        this.storageReference = storageReference;
        this.status = DocumentStatus.GENERATED;
    }

    public void markAsFailed() {
        this.status = DocumentStatus.FAILED;
    }


    //gettery
    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public DocumentType getType() {
        return type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public DocumentStatus getStatus() {
        return status;
    }

    public String getStorageReference() {
        return storageReference;
    }
}
