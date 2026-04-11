package com.bank.payment_service.infrastructure.out.persistence;

import com.bank.payment_service.application.port.out.PaymentRepository;
import com.bank.payment_service.domain.*;
import com.bank.payment_service.infrastructure.entity.PaymentEntity;
import com.bank.payment_service.infrastructure.mapper.PaymentMapper;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PaymentRepositoryAdapter implements PaymentRepository {

    private final SpringDataPaymentRepository repository;
    private final PaymentMapper paymentMapper;

    public PaymentRepositoryAdapter(SpringDataPaymentRepository repository, PaymentMapper paymentMapper) {
        this.repository = repository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return repository.findById(id)
                .map(paymentMapper::toDomain);
    }

    @Override
    public Payment save(Payment payment) {
        PaymentEntity entity = paymentMapper.toEntity(payment);

        PaymentEntity savedEntity = repository.save(entity);

        return paymentMapper.toDomain(savedEntity);
    }
}
