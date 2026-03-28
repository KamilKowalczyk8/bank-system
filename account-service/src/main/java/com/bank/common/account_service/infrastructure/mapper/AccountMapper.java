package com.bank.common.account_service.infrastructure.mapper;

import com.bank.common.account_service.domain.Account;
import com.bank.common.account_service.infrastructure.entity.AccountEntity;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {

    public AccountEntity toEntity(Account account) {
        return new AccountEntity(
                account.getAccountId(),
                account.getCustomerId(),
                account.getAccountNumber().getValue(),
                account.getBalance(),
                account.getCurrency(),
                account.getStatus(),
                account.getCreatedAt()
        );
    }
}
