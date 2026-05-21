package com.bank.document_service.application.service;

import com.bank.document_service.application.port.DocumentEventPublisher;
import com.bank.document_service.application.port.DocumentStoragePort;
import com.bank.document_service.application.port.PasswordGeneratorPort;
import com.bank.document_service.application.port.PdfGeneratorPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
public class GenerateOnboardingDocumentsUseCaseTest {

    @Mock
    private DocumentEventPublisher documentEventPublisher;
    @Mock
    private DocumentStoragePort documentStoragePort;
    @Mock
    private PasswordGeneratorPort passwordGeneratorPort;
    @Mock
    private PdfGeneratorPort pdfGeneratorPort;

    @InjectMocks
    private GenerateOnboardingDocumentsUseCase useCase;

    @Test
    void shouldSuccessfullyGenerateAndPublishDocument() {
        UUID userId = UUID.randomUUID();
        String firstName = "Jan";
        String lastName = "Kowalski";
        String login = "BANK-123456";
        String email = "jan@example.com";
        String phone = "+48123456789";
        String bankTempPassword = "BankPassword!1";

        //symulacaj gotowego hasła
        String expectedDocumentPassword = "DocPassword#9";
        //symulacaj gotowego pliku
        byte[] expectedPdfBytes = new byte[]{1, 2, 3};
        //sciezka
        String expectedSavedPath = "C:/bank-documents/umowa_" + userId + ".pdf";

        given(passwordGeneratorPort.generateTemporaryPassword())
                .willReturn(expectedDocumentPassword);

        given(pdfGeneratorPort.generateContract(userId, firstName, lastName, login, bankTempPassword, expectedDocumentPassword))
                .willReturn(expectedPdfBytes);

        given(documentStoragePort.saveDocument("umowa_" + userId + ".pdf", expectedPdfBytes))
                .willReturn(expectedSavedPath);

        //własciwa funkcja
        useCase.execute(userId, firstName, lastName, login, email, phone, bankTempPassword);

        verify(documentEventPublisher).publishDocumentsReadyEvent(
                email,
                phone,
                expectedSavedPath,
                expectedDocumentPassword
        );

        verify(pdfGeneratorPort).generateContract(
                eq(userId),
                eq(firstName),
                eq(lastName),
                eq(login),
                eq(bankTempPassword),
                eq(expectedDocumentPassword)
        );

    }

    @Test
    void houldThrowExceptionAndNotPublishEventWhenStorageFails() {
        UUID userId = UUID.randomUUID();
        String firstName = "Jan";
        String lastName = "Kowalski";
        String login = "BANK-123456";
        String email = "jan@example.com";
        String phone = "+48123456789";
        String bankTempPassword = "BankPassword!1";

        byte[] expectedPdfBytes = new byte[]{1, 2, 3};

        given(passwordGeneratorPort.generateTemporaryPassword())
                .willReturn("DocPassword#9");

        //any słuzy do symulacji danych
        given(pdfGeneratorPort.generateContract(any(), any(), any(), any(), any(), any()))
                .willReturn(expectedPdfBytes);

        //twoorzymy błąd
        given(documentStoragePort.saveDocument(anyString(), any()))
                .willThrow(new RuntimeException("Krytyczny błąd: Brak miejsca na dysku komputera"));

        //sprawdzamy czy rzuci wyjątek
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            useCase.execute(userId, firstName, lastName, login, email, phone, bankTempPassword);
        });

        assertEquals("Krytyczny błąd: Brak miejsca na dysku komputera", exception.getMessage());

        verifyNoInteractions(documentEventPublisher);
    }
}
