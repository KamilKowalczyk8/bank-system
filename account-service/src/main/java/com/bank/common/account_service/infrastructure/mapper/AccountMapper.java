package com.bank.common.account_service.infrastructure.mapper;

import com.bank.common.account_service.domain.Account;
import com.bank.common.account_service.domain.AccountNumber;
import com.bank.common.account_service.domain.AccountStatus;
import com.bank.common.account_service.domain.Currency;
import com.bank.common.account_service.infrastructure.entity.AccountEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AccountMapper {

    public AccountEntity toEntity(Account account) {
        if (account == null) {
            return null;
        }

        return AccountEntity.builder()
                .accountId(account.getAccountId())
                .customerId(account.getCustomerId())
                .accountNumber(account.getAccountNumber().getValue())
                .balance(account.getBalance())
                .currency(account.getCurrency())
                .status(account.getStatus())
                .createdAt(account.getCreatedAt())
                .build();
    }

    public Account toDomain(AccountEntity entity) {
        if (entity == null) {
            return null;
        }

        return Account.restore(
                entity.getAccountId(),
                entity.getCustomerId(),
                new AccountNumber(entity.getAccountNumber()),
                entity.getBalance(),
                entity.getCurrency(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
