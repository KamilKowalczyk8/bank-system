package com.bank.payment_service.infrastructure.out.external;

import com.bank.payment_service.application.port.out.AccountOperationPort;
import com.bank.payment_service.domain.Money;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountOperationAdapter implements AccountOperationPort {

    @Override
    public boolean reserveFunds(UUID sourceAccountId, Money amount) {
        System.out.println("[ACCOUNT-SERVICE-MOCK] Rezerwuję " + amount.amount() + " " + amount.currency() + " na koncie " + sourceAccountId);
        return true;
    }

}
