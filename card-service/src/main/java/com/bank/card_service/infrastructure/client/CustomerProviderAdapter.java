package com.bank.card_service.infrastructure.client;

import com.bank.card_service.application.port.out.CustomerProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CustomerProviderAdapter implements CustomerProvider {

    private final CustomerClient customerClient;

    public CustomerProviderAdapter(CustomerClient customerClient) {
        this.customerClient = customerClient;
    }

    @Override
    public String getCustomerEmail(UUID accountId) {
        try {
            return customerClient.getCustomerProfile(accountId).email();
        } catch (Exception e) {
            return "nieznany@klient.pl";
        }
    }

}
