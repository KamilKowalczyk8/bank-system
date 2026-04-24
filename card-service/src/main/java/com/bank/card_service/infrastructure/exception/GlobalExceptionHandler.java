package com.bank.card_service.infrastructure.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.bank.common.api.ErrorReporter;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    private final ErrorReporter errorReporter;

    //błedy z @valid kontroler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        String auditMessage = "Odrzuczono zapytanie (Błąd walidacji):" + String.join(", ", errors);
        log.warn(auditMessage);
        errorReporter.report(new IllegalArgumentException(auditMessage));

        ApiErrorResponse response = new ApiErrorResponse(
                "Błąd walidacji danych wejściowych",
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //biznesowe z useCase
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        log.warn("Błąd w regule biznesowej: {}", ex.getMessage());
        errorReporter.report(ex);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }


}
