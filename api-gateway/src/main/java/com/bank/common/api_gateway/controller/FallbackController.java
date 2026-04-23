package com.bank.common.api_gateway.controller;

import com.bank.common.api.ErrorReporter;
import com.bank.common.api_gateway.dto.GatewayErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
@RequiredArgsConstructor
@Slf4j
public class FallbackController {

    private final ErrorReporter errorReporter;

    @Operation(
            summary = "Awaryjna ścieżka dla customer-service",
            description = "Ten endpoint jest wywoływany automatycznie przez Circuit Breaker, gdy mikroserwis zarządzania klientami przestanie odpowiadać."
    )
    @ApiResponse(
            responseCode = "503",
            description = "Usługa niedostępna (Service Unavailable)",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = GatewayErrorResponse.class))
    )
    @RequestMapping(value = "customer", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<GatewayErrorResponse> customerFallback() {
        String msg = "KRYTYCZNE OSTRZEŻENIE: customer-service przestał odpowiadać! Uruchomiono Circuit Breaker.";
        log.error(msg);
        errorReporter.report(new RuntimeException(msg));

        GatewayErrorResponse errorResponse = GatewayErrorResponse.of(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                "System profili klientów jest w tej chwili przeciążony lub niedostępny. Przepraszamy za utrudnienia, spróbuj ponownie za kilka minut.",
                "/api/customers (Fallback)"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
}
