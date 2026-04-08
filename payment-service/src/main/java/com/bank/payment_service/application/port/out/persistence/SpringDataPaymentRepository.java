package com.bank.payment_service.application.port.out.persistence;

import com.bank.payment_service.infrastructure.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPaymentRepository extends JpaRepository<PaymentEntity, UUID> {
}
