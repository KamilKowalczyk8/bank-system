package com.bank.payment_service.infrastructure.out.external.account;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.math.BigDecimal;
import java.util.UUID;

@HttpExchange("/api/accounts")
public interface AccountClient {

    @PostExchange("/{accountId}/reserve")
    void reserveFunds(@PathVariable("accountId")UUID accountId, @RequestBody ReserveRequest request);

    record ReserveRequest(BigDecimal amount, String currency) {}
}
