package com.bank.payment_service.infrastructure.out.external.fraud;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "fraud-service",url = "${services.fraud.url:http://localhost:8082}")
public interface FraudClient {
    @PostMapping("/api/fraud/check")
    FraudResponse check(@RequestBody FraudRequest request);

    record FraudRequest(UUID paymentId, BigDecimal amount, String currency, UUID sourceAccount, UUID destinationAccountId) {}
    record FraudResponse(boolean suspected) {}
}
