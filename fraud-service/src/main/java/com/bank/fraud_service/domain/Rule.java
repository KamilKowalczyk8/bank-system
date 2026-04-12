package com.bank.fraud_service.domain;

public interface Rule {
    RiskResult evaluate(FraudContext context);
}
