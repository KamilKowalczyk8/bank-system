package com.bank.card_service.infrastructure.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.bank.common.api.ErrorReporter;

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class TestErrorController {
    private final ErrorReporter errorReporter;

    @GetMapping("/test-error")
    public String triggerError(@RequestParam(defaultValue = "0") int divisor) {
        try {
            int result = 10 / divisor;
        } catch (Exception e) {
            errorReporter.report(new RuntimeException("KRYTYCZNA AWARIA: Ktoś podzielił przez zero w serwisie kart!", e));
        }
        return "Błąd został wygenerowany i wysłany do Kafki!";
    }
}
