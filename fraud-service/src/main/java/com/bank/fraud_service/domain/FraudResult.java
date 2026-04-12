package com.bank.fraud_service.domain;

import java.util.List;

public record FraudResult(
        FraudDecision decision,
        int totalScore,
        List<String> reasons
) {}
