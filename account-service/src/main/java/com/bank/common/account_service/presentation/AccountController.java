package com.bank.common.account_service.presentation;

import com.bank.common.account_service.application.AccountApplicationService;
import com.bank.common.account_service.domain.Account;
import com.bank.common.account_service.presentation.dto.AccountResponse;
import com.bank.common.account_service.presentation.dto.CreateAccountRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountApplicationService accountApplicationService;

    public AccountController(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {

        Account createAccount = accountApplicationService.createAccount(
                request.customerId(),
                request.currency()
        );

        AccountResponse response = AccountResponse.fromDomain(createAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

    }
}
