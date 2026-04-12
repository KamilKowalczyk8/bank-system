package com.bank.fraud_service.infrastructure.out.repository;

import com.bank.fraud_service.infrastructure.out.entity.FraudAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataFraudAuditRepository extends JpaRepository<FraudAuditEntity, UUID> {
}
