package com.bank.card_service.infrastructure.client;

import com.bank.card_service.infrastructure.dto.CustomerProfileResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.UUID;

@HttpExchange("/api/customers")
public interface CustomerClient {

    @GetExchange("/{id}")
    CustomerProfileResponse getCustomerProfile(@PathVariable("id") UUID id);
}
