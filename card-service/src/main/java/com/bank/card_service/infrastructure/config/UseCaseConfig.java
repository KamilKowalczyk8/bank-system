package com.bank.card_service.infrastructure.config;

import com.bank.card_service.application.port.out.*;
import com.bank.card_service.application.service.ActivateCardUseCase;
import com.bank.card_service.application.service.CreateCardUseCase;
import com.bank.card_service.domain.CardFactory;
import com.bank.card_service.domain.CardRepository;
import com.bank.common.api.ErrorReporter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public CreateCardUseCase createCardUseCase(
            CardRepository cardRepository,
            CardNumberGenerator cardNumberGenerator,
            PinHasher pinHasher,
            CardFactory cardFactory,
            CvvGenerator cvvGenerator,
            CardEventPublisher cardEventPublisher,
            CustomerProvider customerProvider,
            ErrorReporter errorReporter
    ) {
        return new CreateCardUseCase(
                cardRepository,
                cardNumberGenerator,
                pinHasher,
                cardFactory,
                cvvGenerator,
                cardEventPublisher,
                customerProvider,
                errorReporter
        );
    }

    @Bean
    public ActivateCardUseCase activateCardUseCase(CardRepository cardRepository, ErrorReporter errorReporter) {
        return new ActivateCardUseCase(
                cardRepository,
                errorReporter
        );
    }
}
