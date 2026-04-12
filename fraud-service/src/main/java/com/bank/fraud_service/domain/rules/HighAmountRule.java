package com.bank.fraud_service.domain.rules;

import com.bank.fraud_service.domain.FraudContext;
import com.bank.fraud_service.domain.RiskResult;
import com.bank.fraud_service.domain.Rule;

import java.math.BigDecimal;

public class HighAmountRule implements Rule {
    private static final BigDecimal THRESHOLD = new BigDecimal("10000");

    @Override
    public RiskResult evaluate(FraudContext context) {
        if (context.amount().compareTo(THRESHOLD) > 0) {
            return new RiskResult(40, "Wysoka kwota transakcji (powyżej 10000)");
        }
        return RiskResult.noRisk();
    }
}
