package com.bank.card_service.infrastructure.client;

import com.bank.card_service.application.port.out.CustomerProvider;

import java.util.UUID;

public class CustomerProviderAdapter implements CustomerProvider {

    private final CustomerFeignClient customerFeignClient;

    public CustomerProviderAdapter(CustomerFeignClient customerFeignClient) {
        this.customerFeignClient = customerFeignClient;
    }

    @Override
    public String getCustomerEmail(UUID accountId) {
        try {
            return customerFeignClient.getCustomerProfile(accountId).email();
        } catch (Exception e) {
            return "nieznany@klient.pl";
        }
    }

}
