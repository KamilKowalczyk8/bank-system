package com.bank.card_service.application.port.out;

import java.util.UUID;

public interface CustomerProvider {
    String getCustomerEmail(UUID accountId);
}
