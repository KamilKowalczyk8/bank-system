package com.bank.payment_service.infrastructure.out.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.util.UUID;

@FeignClient(name = "account-service", url = "${services.account.url:http://localhost:8081}")
public interface AccountClient {

    @PostMapping("/api/accounts/{accountId}/reserve")
    void reserveFunds(@PathVariable("accountId")UUID accountId, @RequestBody ReserveRequest request);

    record ReserveRequest(BigDecimal amount, String currency) {}
}
