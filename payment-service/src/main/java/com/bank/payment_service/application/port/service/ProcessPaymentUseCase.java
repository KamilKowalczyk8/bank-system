package com.bank.payment_service.application.port.service;

import com.bank.payment_service.application.port.out.AccountOperationPort;
import com.bank.payment_service.application.port.out.FraudCheckPort;
import com.bank.payment_service.application.port.out.PaymentEventPublisher;
import com.bank.payment_service.application.port.out.PaymentRepository;
import com.bank.payment_service.domain.Payment;

import java.util.UUID;

public class ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final AccountOperationPort accountOperationPort;
    private final FraudCheckPort fraudCheckPort;

    public ProcessPaymentUseCase(PaymentRepository paymentRepository, PaymentEventPublisher paymentEventPublisher, AccountOperationPort accountOperationPort, FraudCheckPort fraudCheckPort) {
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.accountOperationPort = accountOperationPort;
        this.fraudCheckPort = fraudCheckPort;
    }

    public void execute(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono płatności o ID: " + paymentId));

        payment.markAsPending();
        payment = paymentRepository.save(payment);


        boolean isFraudulent = fraudCheckPort.isFraudulent(payment);

        if (isFraudulent) {
            payment.rejectAsFraud();
            paymentRepository.save(payment);
            //kafka zdarzenie oszust
            return;
        }


        boolean externalSystemSuccess = accountOperationPort.reserveFunds(
                payment.getSourceAccountId(),
                payment.getMoney()
        );

        if (externalSystemSuccess) {
            payment.complete();
        } else {
            payment.fail();
        }

        payment = paymentRepository.save(payment);

        if (externalSystemSuccess) {
            paymentEventPublisher.publishPaymentCompletedEvent(payment);
        }
    }

}
