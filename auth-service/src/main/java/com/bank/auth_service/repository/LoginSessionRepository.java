package com.bank.auth_service.repository;

import com.bank.auth_service.entity.LoginSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LoginSessionRepository extends JpaRepository<LoginSession, UUID> {
}
