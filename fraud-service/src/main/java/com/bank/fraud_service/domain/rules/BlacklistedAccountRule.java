package com.bank.fraud_service.domain.rules;

import com.bank.fraud_service.domain.FraudContext;
import com.bank.fraud_service.domain.RiskResult;
import com.bank.fraud_service.domain.Rule;

public class BlacklistedAccountRule implements Rule {
    private static final String BLACKLISTED_UUID = "99999999-9999-9999-9999-999999999999";

    @Override
    public RiskResult evaluate(FraudContext context) {
        if (context.destinationAccountId().toString().equals(BLACKLISTED_UUID)) {
            return new RiskResult(100, "Konto odbiorcy znajduje się na globalnej blackliscie kont");
        }
        return RiskResult.noRisk();
    }
}
