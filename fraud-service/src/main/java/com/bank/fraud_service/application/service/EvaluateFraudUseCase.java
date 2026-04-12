package com.bank.fraud_service.application.service;

import com.bank.fraud_service.application.port.out.FraudAuditPort;
import com.bank.fraud_service.domain.*;

import java.util.List;

public class EvaluateFraudUseCase {

    private final List<Rule> rules;
    private final FraudAuditPort fraudAuditPort;

    public EvaluateFraudUseCase(List<Rule> rules, FraudAuditPort fraudAuditPort) {
        this.rules = rules;
        this.fraudAuditPort = fraudAuditPort;
    }

    public FraudResult evaluate(FraudContext context) {
        List<RiskResult> firedRules = rules.stream()
                .map(rule -> rule.evaluate(context))
                .filter(result -> result.score() > 0)
                .toList();

        int totalScore = firedRules.stream()
                .mapToInt(RiskResult::score)
                .sum();

        List<String> reasons = firedRules.stream()
                .map(RiskResult::reason)
                .toList();

        FraudDecision decision;
        if (totalScore >= 100) {
            decision = FraudDecision.REJECT;
        } else if (totalScore >= 80) {
            decision = FraudDecision.CHALLENGE_MANUALLY;
        } else if (totalScore >= 40) {
            decision = FraudDecision.CHALLENGE_SMS;
        } else {
            decision = FraudDecision.ACCEPT;
        }

        FraudResult finalResult = new FraudResult(decision, totalScore, reasons);

        if (decision != FraudDecision.ACCEPT) {
            fraudAuditPort.save(context, finalResult);
        }

        return finalResult;
    }
}
