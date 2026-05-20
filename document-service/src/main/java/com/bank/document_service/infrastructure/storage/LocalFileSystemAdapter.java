package com.bank.document_service.infrastructure.storage;

import com.bank.document_service.application.port.DocumentStoragePort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LocalFileSystemAdapter implements DocumentStoragePort {

    private static final String STORAGE_DIRECTORY = "C:\\projekty\\pdfy\\";

    @Override
    public String saveDocument(String fileName, byte[] content) {
        try {
            Path directoryPath = Paths.get(STORAGE_DIRECTORY);
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            //folder i nazwa pliku
            Path filePath = directoryPath.resolve(fileName);

            //wrzut zawartosci
            Files.write(filePath, content);

            return filePath.toAbsolutePath().toString();

        } catch (IOException e) {
            throw new RuntimeException("Krytyczny błąd zapisu pliku na dysk komputera", e);
        }
    }
}
