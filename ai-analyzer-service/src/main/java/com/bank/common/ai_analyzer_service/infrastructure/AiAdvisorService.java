package com.bank.common.ai_analyzer_service.infrastructure;

import com.bank.common.ai_analyzer_service.dto.ErrorLogEvent;
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
                "You are an expert Java and Spring Boot software engineer. Analyze the following application error.\n" +
                "Microservice: %s\n" +
                "Time: %s\n" +
                "Error Message: %s\n" +
                "Stack Trace: %s\n\n" +
                "Provide a short, precise explanation of the root cause and a concrete solution. Keep it highly technical and concise.",
                event.serviceName(), event.timestamp(), event.errorMessage(), event.stackTrace()
        );

        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();

    }
}
