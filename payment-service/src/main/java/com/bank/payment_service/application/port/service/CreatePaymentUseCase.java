package com.bank.payment_service.application.port.service;

import com.bank.payment_service.application.port.in.CreatePaymentCommand;
import com.bank.payment_service.application.port.out.PaymentRepository;
import com.bank.payment_service.domain.Money;
import com.bank.payment_service.domain.Payment;
import com.bank.payment_service.domain.PaymentType;

public class CreatePaymentUseCase {

    private final PaymentRepository paymentRepository;

    public CreatePaymentUseCase(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment execute(CreatePaymentCommand command) {
        Money money = new Money(command.amount(), command.currency());
        PaymentType paymentType = PaymentType.valueOf(command.type());

        Payment payment = new Payment(
                command.sourceAccountId(),
                command.destinationAccountId(),
                money,
                paymentType
        );

        return paymentRepository.save(payment);
    }
}
