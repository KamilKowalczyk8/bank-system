package com.bank.ai_analyzer_service.infrastructure;

import com.bank.ai_analyzer_service.dto.ErrorLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AiAdvisorService {

    private final ChatClient chatClient;

    public AiAdvisorService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String analyzeError(ErrorLogEvent event) {
        log.info("Wysyłam do lokalnego AI błąd z serwisu: {}", event.serviceName());

        String prompt = String.format(
                "Jesteś ekspertem Java, Spring Boot i znasz się perfekcyjnie na błędnych logach serwisów. Przeanalizuj poniższy błąd aplikacji.\n" +
                "Mikroserwis: %s\n" +
                "Czas: %s\n" +
                "Komunikat błędu: %s\n" +
                "Stack Trace: %s\n\n" +
                "Napisz krótko, co jest przyczyną i podaj jednoznaczne rozwiązanie.",
                event.serviceName(), event.timestamp(), event.errorMessage(), event.stackTrace()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();

    }
}
