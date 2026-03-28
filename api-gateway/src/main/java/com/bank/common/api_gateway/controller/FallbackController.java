package com.bank.common.api_gateway.controller;

import com.bank.common.api_gateway.dto.GatewayErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("customer")
    public ResponseEntity<GatewayErrorResponse> customerFallback() {
        GatewayErrorResponse errorResponse = GatewayErrorResponse.of(
                HttpStatus.SERVICE_UNAVAILABLE.value(),
                HttpStatus.SERVICE_UNAVAILABLE.getReasonPhrase(),
                "System profili klientów jest w tej chwili przeciążony lub niedostępny. Przepraszamy za utrudnienia, spróbuj ponownie za kilka minut.",
                "/api/customers (Fallback)"
        );
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }
}
