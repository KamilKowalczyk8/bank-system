package com.bank.card_service.domain;

import java.util.Optional;
import java.util.UUID;

public interface CardRepository {
    Card save(Card card);
    Optional<Card> findById(UUID id);
    boolean existsByCardNumber(CardNumber cardNumber);
}
