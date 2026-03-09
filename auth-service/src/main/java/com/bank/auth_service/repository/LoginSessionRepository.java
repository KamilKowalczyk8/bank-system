package com.bank.auth_service.repository;

import com.bank.auth_service.entity.LoginSession;
import com.bank.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LoginSessionRepository extends JpaRepository<LoginSession, UUID> {
    @Modifying
    @Query("UPDATE LoginSession s SET s.isUsed = true WHERE s.user = :user AND s.isUsed = false")
    void invalidateAllActiveSessionsForUser(@Param("user") User user);

}
