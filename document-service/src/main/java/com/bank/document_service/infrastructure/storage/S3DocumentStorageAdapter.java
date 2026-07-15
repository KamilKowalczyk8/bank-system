package com.bank.document_service.infrastructure.storage;

import com.bank.common.api.ErrorReporter;
import com.bank.document_service.application.port.DocumentStoragePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
public class S3DocumentStorageAdapter implements DocumentStoragePort {

    private static final Logger log = LoggerFactory.getLogger(S3DocumentStorageAdapter.class);

    private final S3Client s3Client;
    private final ErrorReporter errorReporter;
    private final String bucketName;

    public S3DocumentStorageAdapter(
            S3Client s3Client,
            ErrorReporter errorReporter,
            @Value("${cloud.aws.s3.bucket}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.errorReporter = errorReporter;
        this.bucketName = bucketName;
    }

    @Override
    public String saveDocument(String fileName, byte[] content) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .contentType("application/pdf")
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content));

            log.info("Zapisano dokument w S3 MinIO. Bucket: {}, Klucz: {}", bucketName, fileName);

            return fileName;

        } catch (Exception e) {
            String msg = "Krytyczny błąd zapisu pliku " + fileName + " do MinIO/S3";
            log.error(msg, e);
            errorReporter.report(new RuntimeException(msg, e));
            throw new RuntimeException(msg, e);
        }
    }
}