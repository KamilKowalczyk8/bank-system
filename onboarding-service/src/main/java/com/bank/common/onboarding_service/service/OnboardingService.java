package com.bank.common.onboarding_service.service;

import com.bank.common.api.ErrorReporter;
import com.bank.common.onboarding_service.client.AccountServiceClient;
import com.bank.common.onboarding_service.client.AuthServiceClient;
import com.bank.common.onboarding_service.client.CustomerServiceClient;
import com.bank.common.onboarding_service.dto.*;
import com.bank.common.onboarding_service.event.CustomerRegisteredEvent;
import com.bank.common.onboarding_service.producer.OnboardingEventProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OnboardingService {

    private final AuthServiceClient authServiceClient;
    private final CustomerServiceClient customerServiceClient;
    private final AccountServiceClient accountServiceClient;
    private final OnboardingEventProducer onboardingEventProducer;
    private final ErrorReporter errorReporter;

    public void processOnboarding(OnboardingRequest request) {
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
                request.email(),

                request.phoneNumber(),
                request.street(),
                request.buildingNumber(),
                request.apartmentNumber(),
                request.city(),
                request.zipCode(),
                request.country()
        );

        boolean isCustomerProfileCreated = false;

        try {
            log.info("2. Wysyłam żądanie utworzenia profilu do customer-service...");

            customerServiceClient.createCustomerProfile(customerProfileRequest);
            log.info("Sukces! Profil klienta został pomyślnie utworzony. Proces onboardingu zakończony.");

            isCustomerProfileCreated = true;

            log.info("3. Wysyłam żądanie utworzenia konta bankowego do account-service...");
            AccountCreateRequest accountCreateRequest = new AccountCreateRequest(
                    generatedAuthId,
                    request.currency()
            );

            AccountResponse accountResponse = accountServiceClient.createAccount(accountCreateRequest);
            log.info("Sukces! Proces onboardingu zakończony. Wygenerowano IBAN: {}", accountResponse.accountNumber());

            CustomerRegisteredEvent event = new CustomerRegisteredEvent(
                    generatedAuthId,
                    request.email(),
                    request.pesel()
            );

            onboardingEventProducer.sendCsutomerRegisteredEvent(event);
            log.info("Wysłano powiadomienie na Kafkę o nowym kliencie.");

        } catch (Exception e) {
            log.error("SZCZEGÓŁY BŁĘDU (Z kroku 2 lub 3): {}", e.getMessage());
            log.error("BŁĄD! Przerwano proces. Uruchamiam procedurę kompensacji (rollback) dla authId: {}", generatedAuthId);

            if (isCustomerProfileCreated) {
                try {
                    log.info("Cofanie profilu w customer-service...");
                    customerServiceClient.deleteCustomerProfile(generatedAuthId);
                } catch (Exception ex) {
                    String msg = "KATASTROFALNY BŁĄD (Inconsistent State): Nie udało się cofnąć profilu klienta dla ID " + generatedAuthId + ". " +
                            "Profil został (lub próbowano go) utworzyć w customer-service, ale proces onboardingu upadł. Wymagane ręczne usunięcie z bazy!";
                    log.error(msg, ex);
                    errorReporter.report(new RuntimeException(msg, ex));
                }
            }

            try {
                log.info("Cofanie konta w auth-service...");
                authServiceClient.deleteAccount(generatedAuthId);
            } catch (Exception rollbackException) {
                String msg = "KATASTROFALNY BŁĄD (Inconsistent State): Nie udało się usunąć konta (AuthID: " + generatedAuthId + ") w auth-service podczas rollbacku! " +
                        "Klient istnieje w Auth, ale nie ma profilu. Wymagane ręczne usunięcie z bazy auth!";
                log.error(msg, rollbackException);
                errorReporter.report(new RuntimeException(msg, rollbackException));
            }

            throw new IllegalStateException("Proces onboardingu przerwany i wycofany z powodu błędu w mikroserwisach: " + e.getMessage());
        }
    }
}
