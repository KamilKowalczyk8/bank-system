package com.bank.audit_service.service;

import com.bank.audit_service.dto.SendNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.bank.common.dto.ErrorLogEvent;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditAnalyzerService {

    private final MongoTemplate mongoTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ChatModel chatModel;

    @Scheduled(fixedRate = 15000)
    public void performPeriodicAudit() {
        log.info("🕒 [AUDIT-AI] Uruchamiam okresowy przegląd logów systemowych z MongoDB");

        Instant twoHoursAgo = Instant.now().minus(2, ChronoUnit.HOURS);

        Query query = new Query();
        query.addCriteria(Criteria.where("timestamp").gte(twoHoursAgo)
                .and("level").in("ERROR", "WARN"));

        List<ErrorLogEvent> recentErrors = mongoTemplate.find(query, ErrorLogEvent.class, "error_history");

        if (recentErrors.isEmpty()) {
            log.info("Brak błędów i ostrzeżeń w bazie z ostatnich 2 godzin. System jest stabilny.");
            return;
        }

        log.info("🔍 Znaleziono {} niepokojących zdarzeń w bazie. Przygotowuję raport dla Gemini", recentErrors.size());

        StringBuilder logsSummary = new StringBuilder();
        for (ErrorLogEvent error : recentErrors) {
            logsSummary.append(String.format("- [%s] Serwis: %s | Wiadomość: %s\\n",
            error.level(), error.serviceName(), error.message()));
        }

        ChatClient client = ChatClient.builder(chatModel)
                .defaultSystem("Jesteś ekspertem ds. cyberbezpieczeństwa i systemów rozproszonych.")
                .build();

        String prompt = "Oto lista błędów i ostrzeżeń zebranych z mikroserwisów bankowych z ostatnich 2 godzin:\n\n" +
                logsSummary.toString() + "\n" +
                "Przeanalizuj te dane pod kątem powtarzających się anomalii (np. czy jeden serwis sypie serią błędów, czy widać problem z bazą danych lub siecią). " +
                "Podaj zwięzłe, techniczne podsumowanie kondycji systemu oraz wskaż kluczowe punkty zapalne w języku polskim (użyj Markdown).";

        try {
            String aiReport = client.prompt()
                    .user(prompt)
                    .call()
                    .content();

            publishNotification(aiReport);
        } catch (Exception e) {
            log.error("Błąd AI podczas audytu okresowego, wysyłam raport surowy: {}", e.getMessage());
            String fallbackReport =
                    "**RAPORT OKRESOWY (AI NIEDOSTĘPNE)**\n\n" +
                    "System nie był w stanie wygenerować analizy AI z powodu wyczerpania limitów API. " +
                    "Znaleziono " + recentErrors.size() + " błędów w ostatnich 2 godzinach. " +
                    "Proszę zalogować się do MongoDB i sprawdzić kolekcję `error_history`.";

            publishNotification(fallbackReport);
        }
    }

    private void publishNotification(String report) {
        SendNotificationEvent event = new SendNotificationEvent(
                "RAPORT SYSTEMOWY",
                report,
                List.of("SLACK", "DISCORD")
        );
        kafkaTemplate.send("system-notifications-topic", event);
    }
}
