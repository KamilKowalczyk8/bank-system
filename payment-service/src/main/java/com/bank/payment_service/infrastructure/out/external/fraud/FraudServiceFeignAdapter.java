package com.bank.payment_service.infrastructure.out.external.fraud;

import com.bank.payment_service.application.port.out.FraudCheckPort;
import com.bank.payment_service.domain.Payment;
import org.springframework.stereotype.Component;

@Component
public class FraudServiceFeignAdapter implements FraudCheckPort {

    private final FraudClient fraudClient;

    public FraudServiceFeignAdapter(FraudClient fraudClient) {
        this.fraudClient = fraudClient;
    }

    @Override
    public boolean isFraudulent(Payment payment) {
        try {
            var request = new FraudClient.FraudRequest(
                    payment.getId(),
                    payment.getMoney().amount(),
                    payment.getMoney().currency().name(),
                    payment.getSourceAccountId(),
                    payment.getDestinationAccountId()
            );

            return fraudClient.check(request).suspected();
        } catch (Exception e) {
            return true;
        }
    }
}
