package com.bank.payment_service.application.port.out;

import com.bank.payment_service.domain.Payment;

public interface FraudCheckPort {
    /**
     * @return true jeśli transakcja jest podejrzana (fraud), false jeśli jest bezpieczna.
     */
    boolean isFraudulent(Payment payment);
}
