package com.bank.payment_service.application.port.out;

import com.bank.payment_service.domain.Money;

import java.util.UUID;

public interface AccountOperationPort {
    boolean reserveFunds(UUID sourceAccountId, Money money);
}
