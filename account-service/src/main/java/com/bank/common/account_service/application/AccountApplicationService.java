package com.bank.common.account_service.application;

import com.bank.common.account_service.domain.Account;
import com.bank.common.account_service.domain.AccountNumber;
import com.bank.common.account_service.domain.Currency;
import com.bank.common.account_service.infrastructure.entity.AccountEntity;
import com.bank.common.account_service.infrastructure.mapper.AccountMapper;
import com.bank.common.account_service.infrastructure.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

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

    public void reserveFunds(String accountId, BigDecimal amount) {
        UUID parseId = UUID.fromString(accountId);

        AccountEntity entity = accountRepository.findById(parseId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono konta o ID: " + accountId));

        Account account = accountMapper.toDomain(entity);

        account.reserve(amount);

        AccountEntity entityToUpdate = accountMapper.toEntity(account);

        accountRepository.save(entityToUpdate);
    }

}
