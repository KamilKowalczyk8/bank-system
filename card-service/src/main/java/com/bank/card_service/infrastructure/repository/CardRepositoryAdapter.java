package com.bank.card_service.infrastructure.repository;

import com.bank.card_service.domain.Card;
import com.bank.card_service.domain.CardNumber;
import com.bank.card_service.domain.CardRepository;
import com.bank.card_service.infrastructure.entity.CardEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class CardRepositoryAdapter implements CardRepository {

    private final SpringDataCardRepository springRepository;

    public CardRepositoryAdapter(SpringDataCardRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public Card save(Card card) {

        CardEntity cardEntity = CardEntity.builder()
                .id(card.getId())
                .cardNumber(card.getCardNumber().getValue())
                .accountId(card.getAccountId())
                .status(card.getStatus().name())
                .expiryDate(card.getExpiryDate())
                .pinHash(card.getPinHash())
                .dailyLimit(card.getDailyLimit())
                .build();

        CardEntity savedEntity = springRepository.save(cardEntity);

        return mapToDomain(savedEntity);
    }

    @Override
    public Optional<Card> findById(UUID id) {
        return springRepository.findById(id)
                .map(this::mapToDomain);
    }

    @Override
    public boolean existsByCardNumber(CardNumber cardNumber) {
        return springRepository.existsByCardNumber(cardNumber.getValue());
    }

    private Card mapToDomain(CardEntity entity) {
        return new Card(
                entity.getId(),
                new CardNumber(entity.getCardNumber()),
                entity.getAccountId(),
                entity.getExpiryDate(),
                entity.getPinHash(),
                entity.getDailyLimit(),
                "***"
        );
    }

}
