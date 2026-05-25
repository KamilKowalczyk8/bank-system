package com.bank.document_service.infrastructure.pdf;

import com.bank.common.api.ErrorReporter;
import com.bank.document_service.application.port.PdfGeneratorPort;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

@Component
public class OpenPdfAdapter implements PdfGeneratorPort {

    private final ErrorReporter errorReporter;

    public OpenPdfAdapter(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @Override
    public byte[] generateContract(UUID userId, String firstName, String lastName, String login, String bankTemporaryPassword, String documentPassword) {
        Document document = new Document();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);

            writer.setEncryption(
                    documentPassword.getBytes(),
                    //furtka awaryjna na przyszłość w postaci tego klucza aby pominąćhasło w pryszłosci
                    "bank-internal-admin-key".getBytes(),
                    PdfWriter.ALLOW_PRINTING,
                    PdfWriter.ENCRYPTION_AES_128
            );

            document.open();

            //ustawienia czcionek
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
            Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            Paragraph title = new Paragraph("UMOWA O PROWADZENIE RACHUNKU BANKOWEGO", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            //tresc
            Paragraph body = new Paragraph(
                    "Zawarta pomiędzy KowalczykBank S.A., a Klientem o imieniu: " + firstName + " i nazwisku "+ lastName + ". " +
                            "Bank zobowiązuje się do prowadzenia rachunku płatniczego oraz świadczenia usług bankowości elektronicznej.",
                    regularFont
            );
            body.setSpacingAfter(25);
            document.add(body);

            //dane do pierwszego logowania
            Paragraph credentialsHeader = new Paragraph("TWOJE DANE DO LOGOWANIA", sectionFont);
            credentialsHeader.setSpacingAfter(10);
            document.add(credentialsHeader);

            document.add(new Paragraph("Twoj login (Identyfikator): " + login, regularFont));
            document.add(new Paragraph("Twoje haslo do pierwszego logowania: " + bankTemporaryPassword, regularFont));

            Paragraph warning = new Paragraph(
                    "\n\nUWAGA: Niniejszy dokument został zaszyfrowany. " +
                            "Hasłem dostępu do tego pliku PDF jest Twoje hasło przesłane w wiadomości SMS.",
                    regularFont
            );
            document.add(warning);
        } catch (Exception e) {
            String msg = "KATASTROFALNY BŁĄD (PDF Generation): Nie udało się złożyć pliku PDF dla loginu " + login + ". " +
                    "Błąd podczas składania pliku PDF proces upadł";

            errorReporter.report(new RuntimeException(msg, e));

            throw new RuntimeException("Błąd podczas składania pliku PDF proces upadł", e);
        } finally {
            document.close();
        }

        return outputStream.toByteArray();
    }
}
