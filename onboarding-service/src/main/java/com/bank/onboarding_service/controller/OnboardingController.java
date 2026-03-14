package com.bank.onboarding_service.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
// TODO 1: Dodaj ścieżkę bazową dla tego kontrolera (np. @RequestMapping("/api/onboarding"))
// TODO 2: Dodaj adnotację Lomboka do wstrzyknięcia OnboardingService (@RequiredArgsConstructor)
public class OnboardingController {

    // TODO 3: Zadeklaruj prywatne, finalne pole dla OnboardingService

    // TODO 4: Stwórz metodę z adnotacją @PostMapping("/register")
    // TODO 5: W argumencie metody dodaj @Valid oraz @RequestBody dla OnboardingRequest
    // TODO 6: W ciele metody wywołaj processOnboarding() z Twojego serwisu
    // TODO 7: Zwróć do klienta status HTTP 201 (Created) z wiadomością o sukcesie
}