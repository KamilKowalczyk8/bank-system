package com.bank.common.account_service.infrastructure.repository;

import com.bank.common.account_service.infrastructure.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<AccountEntity, UUID> {
    @Query(value = "SELECT nextval('account_number_sequence')", nativeQuery = true)
    Long getNextAccountNumberSequence();
}
