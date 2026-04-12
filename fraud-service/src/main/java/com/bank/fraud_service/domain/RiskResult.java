package com.bank.fraud_service.domain;

public record RiskResult(int score, String reason) {
    public static RiskResult noRisk() {
        return new RiskResult(0, null);
    }
}
