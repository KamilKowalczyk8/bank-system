package com.bank.common.onboarding_service.dto;

public record AccountCreateRequest(
        String customerId,
        String currency
) {
}
