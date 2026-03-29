package com.bank.card_service.infrastructure.repository;

import com.bank.card_service.infrastructure.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SpringDataCardRepository extends JpaRepository<CardEntity, UUID> {

    boolean existsByCardNumber(String cardNumber);

}
