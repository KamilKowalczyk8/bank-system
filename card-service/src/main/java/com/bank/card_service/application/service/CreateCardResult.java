package com.bank.card_service.application.service;

import com.bank.card_service.domain.Card;

public record CreateCardResult(
        Card card,
        String rawCvv
) {}
