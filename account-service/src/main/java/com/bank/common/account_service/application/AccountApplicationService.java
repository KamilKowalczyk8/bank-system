package com.bank.common.account_service.application;

import com.bank.common.account_service.domain.Account;
import com.bank.common.account_service.domain.AccountNumber;
import com.bank.common.account_service.domain.Currency;
import com.bank.common.account_service.infrastructure.entity.AccountEntity;
import com.bank.common.account_service.infrastructure.mapper.AccountMapper;
import com.bank.common.account_service.infrastructure.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountApplicationService {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    public AccountApplicationService(AccountMapper accountMapper, AccountRepository accountRepository) {
        this.accountMapper = accountMapper;
        this.accountRepository = accountRepository;
    }

    public Account createAccount(String customerId, Currency currency) {
        Long sequenceNumber = accountRepository.getNextAccountNumberSequence();

        String formattedSequence = String.format("%016d", sequenceNumber);

        AccountNumber accountNumber = AccountNumber.generateNew(formattedSequence);

        Account newAccount = new Account(customerId, accountNumber, currency);

        AccountEntity entityToSave = accountMapper.toEntity(newAccount);

        accountRepository.save(entityToSave);

        return newAccount;
    }

}
