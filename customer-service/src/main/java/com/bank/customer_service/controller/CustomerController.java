package com.bank.customer_service.controller;

import com.bank.customer_service.dto.CustomerProfileResponse;
import com.bank.customer_service.dto.CustomerRegistrationRequest;
import com.bank.customer_service.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@Tag(name = "Profile Klientów", description = "Zarządzanie danymi osobowymi i adresowymi")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "Utworzenie nowego profilu", description = "Tworzy profil klienta wraz z adresem na podstawie danych po rejestracji w auth-service.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Profil został pomyślnie utworzony"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji danych wejściowych (np. zły PESEL)"),
            @ApiResponse(responseCode = "409", description = "Konflikt - profil dla tego AuthID lub PESELu już istnieje")
    })
    @PostMapping("/profile")
    public ResponseEntity<Map<String, UUID>> createProfile(
            @Valid @RequestBody CustomerRegistrationRequest request
    ) {
        UUID customerId = customerService.createCustomerProfile(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of("customerId", customerId));
    }

    @Operation(summary = "Pobranie profilu", description = "Zwraca pełne dane klienta na podstawie identyfikatora AuthID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pomyślnie pobrano profil"),
            @ApiResponse(responseCode = "409", description = "Profil nie istnieje")
    })
    @GetMapping("/profile/{authId}")
    public ResponseEntity<CustomerProfileResponse> getProfile(@PathVariable String authId) {
        CustomerProfileResponse response = customerService.getCustomerProfile(authId);

        return ResponseEntity.ok(response);
    }

}
