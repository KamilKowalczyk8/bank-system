package com.bank.common.account_service.infrastructure.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.bank.common.api.ErrorReporter;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

        private final ErrorReporter errorReporter;

        public GlobalExceptionHandler(ErrorReporter errorReporter) {
            this.errorReporter = errorReporter;
        }

        //Błędy walidacji
        @ExceptionHandler(IllegalArgumentException.class)
        public ResponseEntity<String> hadnleIllegalArgument(IllegalArgumentException ex) {
            log.warn("Błąd walidacji: {}", ex.getMessage());
            errorReporter.report(ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        }

        //Błędy biznesowe
        @ExceptionHandler(IllegalStateException.class)
        public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
            log.warn("Błąd w regule biznesowej: {}", ex.getMessage());
            errorReporter.report(ex);
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
        }

        // łapie całą reszte błędów
        @ExceptionHandler(Exception.class)
        public ResponseEntity<String> handleAllUnhandledExceptions(Exception ex) {
            log.error("KRYTYCZNY BŁĄD SYSTEMU: ", ex);
            errorReporter.report(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Wystąpił wewnętrzny błąd serwera. Administratorzy zostali powiadomieni.");
        }

}
