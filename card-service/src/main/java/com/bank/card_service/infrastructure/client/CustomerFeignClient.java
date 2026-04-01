package com.bank.card_service.infrastructure.client;

import com.bank.card_service.infrastructure.dto.CustomerProfileResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "customer-service", url = "${services.customer.url}")
public interface CustomerFeignClient {

    @GetMapping("/api/customers/{id}")
    CustomerProfileResponse getCustomerProfile(@PathVariable("id") UUID id);
}
