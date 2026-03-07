package com.bank.auth_service.repository;

import com.bank.auth_service.entity.LoginAttempt;
import com.bank.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface LoginAttemptRepository extends JpaRepository<LoginAttempt, Long> {
    int countByUserAndSuccessFalseAndAttemptTimeAfter(User user, Instant time);
}
