package com.bank.payment_service.infrastructure.mapper;

import com.bank.payment_service.domain.Money;
import com.bank.payment_service.domain.Payment;
import com.bank.payment_service.infrastructure.entity.PaymentEntity;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public Payment toDomain(PaymentEntity entity) {
        if (entity == null) {
            return null;
        }

        Money money = new Money(entity.getAmount(), entity.getCurrency());

        return new Payment(
                entity.getId(),
                entity.getSourceAccountId(),
                entity.getDestinationAccountId(),
                money,
                entity.getType(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public PaymentEntity toEntity(Payment domain) {
        if (domain == null) {
            return null;
        }

        return PaymentEntity.builder()
                .id(domain.getId())
                .sourceAccountId(domain.getSourceAccountId())
                .destinationAccountId(domain.getDestinationAccountId())
                .amount(domain.getMoney().amount())
                .currency(domain.getMoney().currency())
                .type(domain.getType())
                .status(domain.getStatus())
                .createdAt(domain.getCreatedAt())
                .build();
    }
}
