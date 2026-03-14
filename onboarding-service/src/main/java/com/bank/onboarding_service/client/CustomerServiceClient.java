package com.bank.onboarding_service.client;

import com.bank.onboarding_service.dto.CustomerProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service", url = "http://localhost:8082")
public interface CustomerServiceClient {

    @PostMapping("/api/customers/profile")
    void createCustomerProfile(@RequestBody CustomerProfileRequest request);
}
