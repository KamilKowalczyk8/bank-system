package com.bank.fraud_service.domain;

public enum FraudDecision {
    ACCEPT,
    CHALLENGE_SMS,
    CHALLENGE_MANUALLY,
    REJECT
}
