package com.bank.fraud_service.infrastructure.in.web;

import com.bank.fraud_service.application.service.EvaluateFraudUseCase;
import com.bank.fraud_service.domain.FraudContext;
import com.bank.fraud_service.domain.FraudDecision;
import com.bank.fraud_service.domain.FraudResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fraud")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Fraud Detection API", description = "Endpoints for analyzing payment risks")
public class FraudController {

    private final EvaluateFraudUseCase evaluateFraudUseCase;

    @PostMapping("/check")
    @Operation(
            summary = "Sprawdzenie płatności przez fraud-service",
            description = "Analizuje kontekst płatności w kontekście reguł bezpieczeństwa i zwraca informację, czy transakcja jest podejrzana o oszustwo"
    )
    @ApiResponse(responseCode = "200", description = "Ryzyko analizy wykazało sukcess")
    public FraudResponseDto checkFraud(@RequestBody FraudRequestDto request) {
        log.info("Rozpoczynamy sprawdzanie płatności o id: {}", request.paymentId());

        FraudContext context = new FraudContext(
                request.paymentId(),
                request.sourceAccount(),
                request.destinationAccountId(),
                request.amount(),
                request.currency()
        );

        FraudResult result = evaluateFraudUseCase.evaluate(context);

        log.info("Sprawdzanie płatności o id: {} zakonczyło się. Decyzja: {}, Wynik: {}, Powody: {}",
                request.paymentId(), result.decision(), result.totalScore(), result.reasons());

        boolean isSuspected = result.decision() == FraudDecision.REJECT
                            || result.decision() == FraudDecision.CHALLENGE_MANUALLY
                            || result.decision() == FraudDecision.CHALLENGE_SMS;

        return new FraudResponseDto(isSuspected);
    }




}
