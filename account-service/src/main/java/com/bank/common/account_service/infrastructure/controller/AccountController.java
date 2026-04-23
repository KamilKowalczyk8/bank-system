package com.bank.common.account_service.infrastructure.controller;

import com.bank.common.account_service.application.AccountApplicationService;
import com.bank.common.account_service.domain.Account;
import com.bank.common.account_service.infrastructure.dto.AccountResponse;
import com.bank.common.account_service.infrastructure.dto.CreateAccountRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountApplicationService accountApplicationService;

    public AccountController(AccountApplicationService accountApplicationService) {
        this.accountApplicationService = accountApplicationService;
    }

    @PostMapping
    @Operation(summary = "Utwórz nowe konto", description = "Tworzy nowe konto bankowe dla podanego klienta w określonej walucie.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Konto zostało pomyślnie utworzone"),
            @ApiResponse(responseCode = "400", description = "Błędne dane wejściowe")
    })
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {

        Account createAccount = accountApplicationService.createAccount(
                request.customerId(),
                request.currency()
        );
        AccountResponse response = AccountResponse.fromDomain(createAccount);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{accountId}/reserve")
    @Operation(summary = "Zarezerwuj środki", description = "Tymczasowo zamraża środki na koncie na poczet przelewu (Saga).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Środki zostały pomyślnie zarezerwowane"),
            @ApiResponse(responseCode = "400", description = "Niepoprawne ID konta lub ujemna kwota"),
            @ApiResponse(responseCode = "422", description = "Odmowa: brak wystarczających środków lub konto nieaktywne")
    })
    public ResponseEntity<Void> reserveFunds(
            @PathVariable("accountId") String accountId,
            @RequestBody ReserveRequest request
    ) {
        accountApplicationService.reserveFunds(accountId, request.amount());
        return ResponseEntity.ok().build();
    }

    public record ReserveRequest(BigDecimal amount, String currency) {}
}
