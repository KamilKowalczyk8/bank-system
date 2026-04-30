package com.bank.common.messaging;

import com.bank.common.api.ErrorReporter;
import com.bank.common.dto.ErrorLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public class ErrorEventPublisher implements ErrorReporter {

    private static final Logger log = LoggerFactory.getLogger(ErrorEventPublisher.class);
    private static final String TOPIC = "error-logs-topic";

    private final KafkaTemplate<String, ErrorLogEvent> kafkaTemplate;
    private final String serviceName;

    public ErrorEventPublisher(KafkaTemplate<String, ErrorLogEvent> kafkaTemplate,
                               String serviceName) {
        this.kafkaTemplate = kafkaTemplate;
        this.serviceName = serviceName;
    }

    @Override
    public void report(Throwable ex) {
        String stackTrace = extractStackTrace(ex);

        ErrorLogEvent event = new ErrorLogEvent(
                "v1",
                serviceName,
                "ERROR",
                ex.getMessage() != null ? ex.getMessage() : "Brak komunikatu błędu",
                stackTrace,
                LocalDateTime.now()
        );
        log.info("📤 Wysyłam raport błędu do Kafki z serwisu: {}", serviceName);
        kafkaTemplate.send(TOPIC, event);
    }

    private String extractStackTrace(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        return sw.toString();
    }

}
