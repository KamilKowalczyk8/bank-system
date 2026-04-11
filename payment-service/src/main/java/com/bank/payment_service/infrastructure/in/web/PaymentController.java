package com.bank.payment_service.infrastructure.in.web;

import com.bank.payment_service.application.port.service.CreatePaymentUseCase;
import com.bank.payment_service.application.port.service.ProcessPaymentUseCase;
import com.bank.payment_service.domain.Payment;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments API", description = "Zarządzanie płatnościami i przelewami w systemie bankowym")
public class PaymentController {

    private final CreatePaymentUseCase createPaymentUseCase;
    private final ProcessPaymentUseCase processPaymentUseCase;

    public PaymentController(
            CreatePaymentUseCase createPaymentUseCase,
            ProcessPaymentUseCase processPaymentUseCase
    ) {
        this.createPaymentUseCase = createPaymentUseCase;
        this.processPaymentUseCase = processPaymentUseCase;
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

    @PostMapping("/{paymentId}/process")
    @Operation(summary = "Przetwórz płatność", description = "Wysyła zlecenie rezerwacji środków do account-service i kończy przelew (sukces lub odrzucenie).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Płatność przetworzona (sukces lub odrzucenie z braku środków zapisało się w bazie)"),
            @ApiResponse(responseCode = "400", description = "Błędny format ID płatności"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono płatności o podanym ID")
    })
    public ResponseEntity<Void> processPayment(@PathVariable("paymentId") UUID paymentId) {
        processPaymentUseCase.execute(paymentId);

        return ResponseEntity.ok().build();
    }
}
