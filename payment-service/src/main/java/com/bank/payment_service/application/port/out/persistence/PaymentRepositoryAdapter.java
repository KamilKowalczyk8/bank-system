package com.bank.payment_service.application.port.out.persistence;

import com.bank.payment_service.application.port.out.PaymentRepository;
import com.bank.payment_service.domain.*;
import com.bank.payment_service.infrastructure.entity.PaymentEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentRepository repository;

    public PaymentRepositoryAdapter(SpringDataPaymentRepository repository) {
        this.repository = repository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = PaymentEntity.builder()
                .id(payment.getId())
                .sourceAccountId(payment.getSourceAccountId())
                .destinationAccountId(payment.getDestinationAccountId())
                .amount(payment.getMoney().amount())
                .currency(payment.getMoney().currency().name())
                .type(payment.getType())
                .status(payment.getStatus())
                .createdAt(payment.getCreatedAt())
                .build();

        PaymentEntity savedEntity = repository.save(entity);

        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return repository.findById(id).map(this::mapToDomain);
    }

    private Payment mapToDomain(PaymentEntity entity) {
        Currency currency = Currency.valueOf(entity.getCurrency());
        Money money = new Money(entity.getAmount(), currency);

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
}
