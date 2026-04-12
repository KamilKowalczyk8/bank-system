package com.bank.fraud_service.application.port.out;

import com.bank.fraud_service.domain.FraudContext;
import com.bank.fraud_service.domain.FraudResult;

public interface FraudAuditPort {
    void save(FraudContext context, FraudResult result);
}
