package com.bank.common.exception;

import com.bank.common.api.ErrorReporter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.LOWEST_PRECEDENCE) // ustalenie priorytetu na najnizszy żeby uniknąc konfliktu z lokalnymi handlerami te uruchamiają się dopiero gdy w danym serwisie brakuje jakiejs obsługi błędu
public class CommonFallbackExceptionHandler {

        private final ErrorReporter errorReporter;

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
            log.error("KRYTYCZNY błąd systemu: ", ex);
            errorReporter.report(ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Wystąpił wewnętrzny błąd serwera. Administratorzy zostali powiadomieni.");
        }

}
