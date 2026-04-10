package com.bank.common.account_service.infrastructure.config;

import com.bank.common.account_service.application.AccountApplicationService;
import com.bank.common.account_service.infrastructure.mapper.AccountMapper;
import com.bank.common.account_service.infrastructure.repository.AccountRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccountUseCaseConfig {

    @Bean
    public AccountApplicationService accountApplicationService(AccountRepository accountRepository, AccountMapper accountMapper) {
        return new AccountApplicationService(accountMapper ,accountRepository);
    }
}
