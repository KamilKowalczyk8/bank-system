package com.bank.onboarding_service.service;

import com.bank.onboarding_service.client.AuthServiceClient;
import com.bank.onboarding_service.client.CustomerServiceClient;
import com.bank.onboarding_service.dto.AuthRegistrationRequest;
import com.bank.onboarding_service.dto.AuthResponse;
import com.bank.onboarding_service.dto.CustomerProfileRequest;
import com.bank.onboarding_service.dto.OnboardingRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

    private final AuthServiceClient authServiceClient;
    private final CustomerServiceClient customerServiceClient;

    public void processsOnboarding(OnboardingRequest request) {
        log.info("Rozpoczynamy proces onboardingu dla email: {}", request.email());

        AuthRegistrationRequest authRequest = new AuthRegistrationRequest(
                request.email(),
                request.phoneNumber()
        );

        log.info("1. Wysyłam żądanie utworzenia konta do auth-service...");
        AuthResponse authResponse = authServiceClient.registerAccount(authRequest);

        String generatedAuthId = authResponse.authId();
        log.info("Sukces! Z auth-service otrzymano authId: {}", generatedAuthId);

        CustomerProfileRequest customerProfileRequest = new CustomerProfileRequest(
                generatedAuthId,
                request.firstName(),
                request.lastName(),
                request.pesel(),
                request.phoneNumber(),
                request.street(),
                request.buildingNumber(),
                request.apartmentNumber(),
                request.city(),
                request.zipCode(),
                request.country()
        );


        try {
            log.info("2. Wysyłam żądanie utworzenia profilu do customer-service...");

            customerServiceClient.createCustomerProfile(customerProfileRequest);
            log.info("Sukces! Profil klienta został pomyślnie utworzony. Proces onboardingu zakończony.");
        } catch (Exception e) {
            log.error("BŁĄD! Odrzucono profil klienta. Uruchamiam procedurę kompensacji dla authId: {}", generatedAuthId);

            try {
                authServiceClient.deleteAccount(generatedAuthId);
                log.info("Rollback udany. Usunięto osierocone konto z auth-service.");
            } catch (Exception rollbackException) {
                log.error("FATAL ERROR: Rollback się nie udał! Mamy konto-zombie w systemie dla authId: {}", generatedAuthId, rollbackException);
            }
            throw new IllegalStateException("Proces onboardingu przerwany z powodu błędu w customer-service.");
        }
    }
}
