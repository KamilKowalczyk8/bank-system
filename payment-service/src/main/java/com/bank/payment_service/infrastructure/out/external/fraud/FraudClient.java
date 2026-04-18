package com.bank.payment_service.infrastructure.out.external.fraud;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.math.BigDecimal;
import java.util.UUID;

@HttpExchange("/api/fraud")
public interface FraudClient {

    @PostExchange("/check")
    FraudResponse check(@RequestBody FraudRequest request);

    record FraudRequest(UUID paymentId, BigDecimal amount, String currency, UUID sourceAccount, UUID destinationAccountId) {}
    record FraudResponse(boolean suspected) {}
}
