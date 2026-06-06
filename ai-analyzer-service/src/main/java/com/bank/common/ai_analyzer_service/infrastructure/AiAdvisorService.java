package com.bank.common.ai_analyzer_service.infrastructure;

import com.bank.common.dto.ErrorLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiAdvisorService {

    private final ChatClient chatClient;

    public AiAdvisorService(ChatClient.Builder chatClientBuilder, ChatModel chatModel) {
        this.chatClient = chatClientBuilder
                .defaultSystem("Jesteś ekspertem w dziedzinie inżynierii oprogramowania Java i Spring Boot.")
                .build();
    }

    public String analyzeError(ErrorLogEvent event) {
        log.info("Wysyłam zapytanie do Gemini API o analizę błędu z serwisu: {}", event.serviceName());

        String prompt = String.format(
                "Jesteś ekspertem w dziedzinie inżynierii oprogramowania Java i Spring Boot. Przeanalizuj poniższy błąd aplikacji.\n" +
                        "Mikroserwis: %s\n" +
                        "Czas: %s\n" +
                        "Komunikat błędu: %s\n" +
                        "Stack Trace: %s\n\n" +
                        "Podaj krótkie, precyzyjne wyjaśnienie głównej przyczyny (root cause) oraz konkretne rozwiązanie (kod lub kroki naprawcze). " +
                        "Odpowiedź musi być wysoce techniczna, zwięzła i sformatowana w języku polskim z użyciem czytelnych punktów (Markdown).",
                event.serviceName(), event.timestamp(), event.message(), event.stackTrace()
        );

        try {
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.warn("AI niedostępne (możliwy limit API). Generuję raport awaryjny.");
            return  "**UWAGA: Moduł AI jest obecnie niedostępny (limit kwoty API).**\n\n" +
                    "Oto surowe dane błędu:\n" +
                    "- **Serwis:** " + event.serviceName() + "\n" +
                    "- **Komunikat:** " + event.message() + "\n" +
                    "- **Czas:** " + event.timestamp() + "\n\n" +
                    "Proszę sprawdź logi w serwisie, aby uzyskać pełny stack trace.";
        }

    }
}
