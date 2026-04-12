package com.bank.payment_service.application.port.out;

import com.bank.payment_service.domain.Payment;

public interface FraudCheckPort {
    boolean isFraudulent(Payment payment);
}
