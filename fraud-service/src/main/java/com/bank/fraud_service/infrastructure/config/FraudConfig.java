package com.bank.fraud_service.infrastructure.config;

import com.bank.common.api.ErrorReporter;
import com.bank.fraud_service.application.port.out.FraudAuditPort;
import com.bank.fraud_service.application.service.EvaluateFraudUseCase;
import com.bank.fraud_service.domain.Rule;
import com.bank.fraud_service.domain.rules.BlacklistedAccountRule;
import com.bank.fraud_service.domain.rules.HighAmountRule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class FraudConfig {

    @Bean
    public EvaluateFraudUseCase evaluateFraudUseCase(FraudAuditPort fraudAuditPort, ErrorReporter errorReporter) {
        List<Rule> ruleList = List.of(
                new HighAmountRule(),
                new BlacklistedAccountRule()
        );

        return new EvaluateFraudUseCase(
                ruleList,
                fraudAuditPort,
                errorReporter
        );
    }
}
