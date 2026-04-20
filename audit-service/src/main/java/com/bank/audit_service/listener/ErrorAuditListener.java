package com.bank.audit_service.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.bank.common.dto.ErrorLogEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class ErrorAuditListener {

    private final MongoTemplate mongoTemplate;

    @KafkaListener(topics = "error-logs-topic", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeErrorLog(ErrorLogEvent event) {
        log.info("[AUDIT] Otrzymano nowy błąd z serwisu: {}", event.serviceName());
        log.info("Szczegóły: [{}] {}", event.level(), event.message());

        mongoTemplate.save(event, "error_history");
        log.info("Błąd pomyślnie zarchiwizowany w bazie MongoDB.");
    }
}
