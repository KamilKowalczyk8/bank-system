package com.bank.payment_service.infrastructure.out.messaging;

import com.bank.payment_service.application.port.out.PaymentEventPublisher;
import com.bank.payment_service.domain.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventPublisherAdapter implements PaymentEventPublisher {

    @Override
    public void publishPaymentCompletedEvent(Payment payment) {
        System.out.println("[KAFKA-MOCK] 🚀 Wysłano zdarzenie o zakończeniu płatności: " + payment.getId());
    }
}
