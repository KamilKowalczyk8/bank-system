package com.bank.payment_service.application.port.out;

import com.bank.payment_service.domain.Payment;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
}
