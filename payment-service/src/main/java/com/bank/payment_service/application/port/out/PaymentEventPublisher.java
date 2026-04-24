package com.bank.payment_service.application.port.out;

import com.bank.payment_service.domain.Payment;

public interface PaymentEventPublisher {
    void publishPaymentCompletedEvent(Payment payment);
    void publishPaymentFailedEvent(Payment payment);
}
