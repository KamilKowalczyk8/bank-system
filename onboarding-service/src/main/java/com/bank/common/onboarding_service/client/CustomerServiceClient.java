package com.bank.common.onboarding_service.client;

import com.bank.onboarding_service.dto.CustomerProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service", url = "${services.customer.url}")
public interface CustomerServiceClient {

    @PostMapping("/api/customers/profile")
    void createCustomerProfile(@RequestBody CustomerProfileRequest request);

    @DeleteMapping("/api/customers/{customerId}")
    void deleteCustomerProfile(@PathVariable("customerId") String customerId);
}
