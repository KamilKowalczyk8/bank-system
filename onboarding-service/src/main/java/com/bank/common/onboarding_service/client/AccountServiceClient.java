package com.bank.common.onboarding_service.client;

import com.bank.common.onboarding_service.dto.AccountCreateRequest;
import com.bank.common.onboarding_service.dto.AccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "account-service", url = "${services.account.url}")
public interface AccountServiceClient {

    @PostMapping("/api/accounts")
    AccountResponse createAccount(@RequestBody AccountCreateRequest request);

}
