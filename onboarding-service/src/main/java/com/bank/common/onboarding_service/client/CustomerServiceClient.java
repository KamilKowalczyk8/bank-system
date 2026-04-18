package com.bank.common.onboarding_service.client;

import com.bank.common.onboarding_service.dto.CustomerProfileRequest;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.DeleteExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange("/api/customers")
public interface CustomerServiceClient {

    @PostExchange("/profile")
    void createCustomerProfile(@RequestBody CustomerProfileRequest request);

    @DeleteExchange("/{customerId}")
    void deleteCustomerProfile(@PathVariable("customerId") String customerId);
}
