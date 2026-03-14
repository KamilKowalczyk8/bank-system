package com.bank.onboarding_service.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO 1: Skopiuj rekord ErrorResponse, który stworzyliśmy wczoraj w customer-service, i wklej go do tego pakietu.

    // TODO 2: Napisz metodę z adnotacją @ExceptionHandler(IllegalStateException.class)
    // TODO 3: Wewnątrz metody złap błąd, który wyrzuca nasz OnboardingService podczas awarii (Saga Rollback)
    // TODO 4: Zwróć do użytkownika ładnego JSON-a (ErrorResponse) ze statusem HTTP 400 (Bad Request) zamiast brzydkiego błędu serwera 500
}