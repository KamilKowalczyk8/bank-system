package com.bank.card_service.infrastructure.config;

import com.bank.card_service.domain.CardFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class CardDomainConfig {

    @Bean
    public CardFactory cardFactory(
            @Value("${bank.card.default-validity-years}") int validityYears,
            @Value("${bank.card.default-daily-limit}") BigDecimal dailyLimit
    ) {
        return new CardFactory(validityYears, dailyLimit);
    }

}
