package com.bank.payment_service.infrastructure.out.messaging;

import com.bank.payment_service.application.port.out.PaymentEventPublisher;
import com.bank.payment_service.domain.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PaymentEventPublisherAdapter implements PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public PaymentEventPublisherAdapter(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishPaymentCompletedEvent(Payment payment) {
        PaymentCompletedEvent paymentCompletedEvent = new PaymentCompletedEvent(
                payment.getId(),
                payment.getSourceAccountId(),
                payment.getDestinationAccountId(),
                payment.getMoney().amount(),
                payment.getMoney().currency().name()
        );

        kafkaTemplate.send("payment.completed", paymentCompletedEvent.sourceAccountId().toString(), paymentCompletedEvent);

        log.info("[KAFKA] Wysłano zdarzenie: Przelew {} zakonczony sukcesem!", payment.getId());
    }

    @Override
    public void publishPaymentFailedEvent(Payment payment) {
        PaymentFailedEvent paymentFailedEvent = new PaymentFailedEvent(
                payment.getId(),
                payment.getSourceAccountId(),
                payment.getDestinationAccountId(),
                payment.getMoney().amount(),
                payment.getMoney().currency().name(),
                payment.getStatus().name()
        );

        kafkaTemplate.send("payment.failed", paymentFailedEvent.sourceAccountId().toString(), paymentFailedEvent);
        log.warn("[KAFKA] Wysłano zdarzenie: Przelew {} ODRZUCONY (Powód: {})",
                payment.getId(), payment.getStatus().name());

    }
}
