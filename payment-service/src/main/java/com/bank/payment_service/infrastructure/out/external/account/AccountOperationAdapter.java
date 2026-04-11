package com.bank.payment_service.infrastructure.out.external.account;

import com.bank.payment_service.application.port.out.AccountOperationPort;
import com.bank.payment_service.domain.Money;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class AccountOperationAdapter implements AccountOperationPort {

    private final AccountClient accountClient;

    public AccountOperationAdapter(AccountClient accountClient) {
        this.accountClient = accountClient;
    }

    @Override
    public boolean reserveFunds(UUID sourceAccountId, Money amount) {
        try {
            AccountClient.ReserveRequest request = new AccountClient.ReserveRequest(
                    amount.amount(),
                    amount.currency().name()
            );

            accountClient.reserveFunds(sourceAccountId, request);
            return true;
        } catch (FeignException e) {
            log.error("[FEIGN] Odrzucenie transakcji w account-service:{}", e.status(), e);
            return false;
        }


    }

}
