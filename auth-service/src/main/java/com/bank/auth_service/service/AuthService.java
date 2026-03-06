package com.bank.auth_service.service;

import com.bank.auth_service.dto.LoginStep1Request;
import com.bank.auth_service.dto.LoginStep1Response;
import com.bank.auth_service.dto.RegisterRequest;
import com.bank.auth_service.dto.RegisterResponse;
import com.bank.auth_service.entity.User;
import com.bank.auth_service.entity.UserRole;
import com.bank.auth_service.entity.UserStatus;
import com.bank.auth_service.exception.AccountBlockedException;
import com.bank.auth_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        String login;
        do {
            login = generateNumericLogin();
        } while (userRepository.findByLogin(login).isPresent());

        String tempPassword = generateTempPassword();
        String hash = passwordEncoder.encode(tempPassword);

        User bankUser = User.builder()
                .login(login)
                .phoneNumber(request.phoneNumber())
                .passwordHash(hash)
                .tempPassword(true)
                .status(UserStatus.PENDING)
                .role(UserRole.USER)
                .failedLoginAttempts(0)
                .build();

        userRepository.save(bankUser);

        log.info("=== NOWE KONTO BANKOWE (SYMULACJA WIADOMOŚCI NA MAILA) ===");
        log.info("Wiadomość wysłana na numer: {}", request.phoneNumber());
        log.info("Twój bankowy login: {}", login);
        log.info("Hasło tymczasowe: {}", tempPassword);
        log.info("==========================================");

        return new RegisterResponse(
                login,
                "Konto utworzone. Hasło zostało wysłane drogą mailową"
        );
    }

    private String generateNumericLogin() {
        int numericId = 10000000 + SECURE_RANDOM.nextInt(90000000);
        return String.valueOf(numericId);
    }

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int randomIndex = SECURE_RANDOM.nextInt(TEMP_PASSWORD_CHARS.length());
            sb.append(TEMP_PASSWORD_CHARS.charAt(randomIndex));
        }
        return sb.toString();
    }

    public LoginStep1Response verifyLoginStep1(LoginStep1Request request) {
        log.info("Rozpoczęto próbę logowania dla loginu: {}", request.login());

        Optional<User> userOptional = userRepository.findByLogin(request.login());

        if (userOptional.isEmpty()) {
            log.warn("Próba logowania na nieistniejący login: {}", request.login());
        } else {
            User user = userOptional.get();
            log.info("Login {} odnaleziony. Status: {}. Oczekiwanie na hasło.", user.getLogin(), user.getStatus());
        }

        return new LoginStep1Response(
                request.login(),
                "PROVIDE_PASSWORD",
                "Podaj hasło"
        );

    }

}
