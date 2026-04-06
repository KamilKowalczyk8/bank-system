package com.bank.card_service.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    //błedy z @valid kontroler
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {

        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ApiErrorResponse response = new ApiErrorResponse(
                "Błąd walidacji danych wejściowych",
                errors,
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    //biznesowe z useCase
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(IllegalStateException ex) {

        ApiErrorResponse response = new ApiErrorResponse(
                "Błąd logiki biznesowej",
                Collections.singletonList(ex.getMessage()),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }


}
