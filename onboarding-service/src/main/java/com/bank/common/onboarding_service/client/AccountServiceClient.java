package com.bank.common.onboarding_service.client;

import com.bank.common.onboarding_service.dto.AccountCreateRequest;
import com.bank.common.onboarding_service.dto.AccountResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/accounts")
public interface AccountServiceClient {

    @PostExchange("")
    AccountResponse createAccount(@RequestBody AccountCreateRequest request);

}
