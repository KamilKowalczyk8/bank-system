package com.bank.payment_service.infrastructure.in.web;

import com.bank.payment_service.application.port.service.CreatePaymentUseCase;
import com.bank.payment_service.domain.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments API", description = "Zarządzanie płatnościami i przelewami w systemie bankowym")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;

    public PaymentController(CreatePaymentUseCase createPaymentUseCase) {
        this.createPaymentUseCase = createPaymentUseCase;
    }

    @PostMapping
    @Operation(summary = "Zainicjuj nową płatność", description = "Rozpoczyna proces płatności (przelew lub karta). Zwraca ID wygenerowanej transakcji.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Płatność pomyślnie zainicjowana"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji danych wejściowych (np. zła waluta)"),
            @ApiResponse(responseCode = "409", description = "Błąd logiki biznesowej (np. to samo konto źródłowe i docelowe)")
    })
    public ResponseEntity<UUID> initiatePayment(@Valid @RequestBody PaymentRequestDto request) {
        Payment createPayment = createPaymentUseCase.execute(request.toCommand());

        return ResponseEntity.ok(createPayment.getId());
    }
}
