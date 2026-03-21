package com.bank.onboarding_service.dto;

public record AccountCreateRequest(
        String customerId,
        String currency
) {
}
