package com.bank.common.auth_service.scheduler;

import com.bank.common.auth_service.repository.LoginSessionRepository;
import com.bank.common.auth_service.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class SessionCleanupScheduler {

    private final LoginSessionRepository loginSessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanUpOldSessions() {
        log.info("Rozpoczynam nocne czyszczenie wygasłych sesji SMS...");

        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);

        loginSessionRepository.deleteAllSessionsOlderThan(sevenDaysAgo);

        refreshTokenRepository.deleteAllTokenOlderThan(sevenDaysAgo);

        log.info("Zakończono nocne czyszczenie starych sesji.");

    }
}
