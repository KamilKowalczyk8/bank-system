package com.bank.fraud_service.infrastructure.out.adapter;

import com.bank.fraud_service.application.port.out.FraudAuditPort;
import com.bank.fraud_service.domain.FraudContext;
import com.bank.fraud_service.domain.FraudResult;
import com.bank.fraud_service.infrastructure.out.entity.FraudAuditEntity;
import com.bank.fraud_service.infrastructure.out.repository.SpringDataFraudAuditRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FraudAuditRepositoryAdapter implements FraudAuditPort {

    private final SpringDataFraudAuditRepository repository;

    @Override
    public void save(FraudContext context, FraudResult result) {

        String joinedReasons = String.join(" | ", result.reasons());

        FraudAuditEntity entity = FraudAuditEntity.builder()
                .paymentId(context.paymentId())
                .sourceAccountId(context.sourceAccountId())
                .destinationAccountId(context.destinationAccountId())
                .decision(result.decision().name())
                .riskScore(result.totalScore())
                .reasons(joinedReasons)
                .build();

        repository.save(entity);
    }
}
