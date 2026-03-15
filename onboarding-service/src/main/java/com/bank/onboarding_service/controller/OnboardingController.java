package com.bank.onboarding_service.controller;

import com.bank.onboarding_service.dto.OnboardingRequest;
import com.bank.onboarding_service.service.OnboardingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/onboarding")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingService onboardingService;

    @Operation(
            summary = "Rejestracja nowego klienta (One-Click)",
            description = "Przyjmuje pełne dane klienta. Tworzy dane logowania w auth-service, a następnie profil w customer-service. W przypadku błędu wycofuje transakcję (Wzorzec Saga)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Pomyślnie utworzono konto i profil klienta"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji danych wejściowych lub proces przerwany")
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid OnboardingRequest request) {
        onboardingService.processsOnboarding(request);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}