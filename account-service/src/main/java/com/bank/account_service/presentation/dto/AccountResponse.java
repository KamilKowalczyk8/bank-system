package com.bank.account_service.presentation.dto;

import com.bank.account_service.domain.Account;
import org.springframework.web.util.HtmlUtils;

import java.math.BigDecimal;

public record AccountResponse(
        String accountId,
        String customerId,
        String accountNumber,
        BigDecimal balance,
        String currency,
        String status
) {
    public static AccountResponse fromDomain(Account account) {
        return new AccountResponse(
                account.getAccountId().toString(),
                HtmlUtils.htmlEscape(account.getCustomerId()),
                account.getAccountNumber().getValue(),
                account.getBalance(),
                account.getCurrency().name(),
                account.getStatus().name()
        );
    }
}
