package com.bank.payment_service.application.port.service;

import com.bank.common.api.ErrorReporter;
import com.bank.payment_service.application.port.out.AccountOperationPort;
import com.bank.payment_service.application.port.out.FraudCheckPort;
import com.bank.payment_service.application.port.out.PaymentEventPublisher;
import com.bank.payment_service.application.port.out.PaymentRepository;
import com.bank.payment_service.domain.Payment;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
public class ProcessPaymentUseCase {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final AccountOperationPort accountOperationPort;
    private final FraudCheckPort fraudCheckPort;
    private final ErrorReporter errorReporter;

    public ProcessPaymentUseCase(PaymentRepository paymentRepository, PaymentEventPublisher paymentEventPublisher, AccountOperationPort accountOperationPort, FraudCheckPort fraudCheckPort, ErrorReporter errorReporter) {
        this.paymentRepository = paymentRepository;
        this.paymentEventPublisher = paymentEventPublisher;
        this.accountOperationPort = accountOperationPort;
        this.fraudCheckPort = fraudCheckPort;
        this.errorReporter = errorReporter;
    }

    public void execute(UUID paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Nie znaleziono płatności o ID: " + paymentId));

        payment.markAsPending();
        payment = paymentRepository.save(payment);

        log.info("Sprawdzanie płatności {} w systemie Fraud", paymentId);
        boolean isFraudulent = fraudCheckPort.isFraudulent(payment);

        if (isFraudulent) {
            log.warn("Płatność {} oflagowana jako FRAUD! Odrzucanie", paymentId);
            payment.rejectAsFraud();
            paymentRepository.save(payment);

            String msg = "ALARM SECURITY: Płatność " + paymentId + " została zablokowana przez system regułowy Fraud Service.";
            errorReporter.report(new SecurityException(msg));
            paymentEventPublisher.publishPaymentFailedEvent(payment);

            return;
        }

        log.info("Rezerwacja środków dla płatności {}", paymentId);

        boolean externalSystemSuccess = accountOperationPort.reserveFunds(
                payment.getSourceAccountId(),
                payment.getMoney()
        );

        try {
            if (externalSystemSuccess) {
                payment.complete();
            } else {
                payment.fail();
            }
            payment = paymentRepository.save(payment);

            if (externalSystemSuccess) {
                paymentEventPublisher.publishPaymentCompletedEvent(payment);
            } else {
                paymentEventPublisher.publishPaymentFailedEvent(payment);
            }

        } catch (Exception e) {
            if (externalSystemSuccess) {
                String msg = "Krytyczny błąd: Środki dla płatności " + paymentId +
                        " zostały zablokowane w account-service, ale payment-service uległ awarii podczas zapisu! " +
                        "Wymagana natychmiastowa poprawka ręczna!";
                log.error(msg, e);
                errorReporter.report(new RuntimeException(msg, e));
            }
            throw e;
        }
    }

}
