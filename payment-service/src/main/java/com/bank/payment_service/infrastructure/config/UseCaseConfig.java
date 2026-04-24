package com.bank.payment_service.infrastructure.config;

import com.bank.common.api.ErrorReporter;
import com.bank.payment_service.application.port.out.AccountOperationPort;
import com.bank.payment_service.application.port.out.FraudCheckPort;
import com.bank.payment_service.application.port.out.PaymentEventPublisher;
import com.bank.payment_service.application.port.out.PaymentRepository;
import com.bank.payment_service.application.port.service.CreatePaymentUseCase;
import com.bank.payment_service.application.port.service.ProcessPaymentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreatePaymentUseCase createPaymentUseCase(PaymentRepository paymentRepository) {
        return new CreatePaymentUseCase(paymentRepository);
    }

    @Bean
    public ProcessPaymentUseCase processPaymentUseCase(
        PaymentRepository paymentRepository,
        PaymentEventPublisher paymentEventPublisher,
        FraudCheckPort fraudCheckPort,
        AccountOperationPort accountOperationPort,
        ErrorReporter errorReporter
        ) {
       return new ProcessPaymentUseCase(
               paymentRepository,
               paymentEventPublisher,
               accountOperationPort,
               fraudCheckPort,
               errorReporter
       );
    }

}
