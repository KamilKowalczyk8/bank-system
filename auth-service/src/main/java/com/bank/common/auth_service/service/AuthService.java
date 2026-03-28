package com.bank.common.auth_service.service;

import com.bank.auth_service.dto.*;
import com.bank.auth_service.entity.*;
import com.bank.common.auth_service.dto.*;
import com.bank.common.auth_service.entity.*;
import com.bank.common.auth_service.exception.AccountBlockedException;
import com.bank.common.auth_service.exception.InvalidCredentialsException;
import com.bank.common.auth_service.repository.LoginAttemptRepository;
import com.bank.common.auth_service.repository.LoginSessionRepository;
import com.bank.common.auth_service.repository.RefreshTokenRepository;
import com.bank.common.auth_service.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptRepository loginAttemptRepository;
    private final LoginSessionRepository loginSessionRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final String dummyHash;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final String TEMP_PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, LoginAttemptRepository loginAttemptRepository, LoginSessionRepository loginSessionRepository, RefreshTokenRepository refreshTokenRepository,JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.loginAttemptRepository = loginAttemptRepository;
        this.loginSessionRepository = loginSessionRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.dummyHash = passwordEncoder.encode("dummy-password-for-timing-attack");
        this.jwtService = jwtService;
    }



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
                .email(request.email())
                .passwordHash(hash)
                .tempPassword(true)
                .status(UserStatus.PENDING)
                .role(UserRole.USER)
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


    public void deleteAccountHard(String authId) {
        log.warn("Otrzymano żądanie usunięcia konta dla authId: {}", authId);

        var account = userRepository.findByLogin(authId)
                .orElseThrow(() -> new IllegalStateException("Nie znaleziono konta do usunięcia dla ID: " + authId));

        userRepository.delete(account);
        log.info("Konto o ID: {} zostało pomyślnie usunięte z bazy (Saga Rollback).", authId);
    }


    public LoginStep1Response verifyLoginStep1(LoginStep1Request request) {
        log.info("Rozpoczęto próbę logowania dla loginu: {}", request.login());

        Optional<User> userOptional = userRepository.findByLogin(request.login());

        if (userOptional.isEmpty()) {
            log.warn("Nieudana próba logowania");
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

    public LoginStep2Response verifyLoginStep2(LoginStep2Request request) {
        log.info("Rozpoczęto weryfikacje hasła dla loginu {}", request.login());

        Optional<User> userOptional = userRepository.findByLogin(request.login());
        User user = userOptional.orElse(null);

        String hashToVerify = user != null ? user.getPasswordHash() : dummyHash;

        boolean passwordMatches = passwordEncoder.matches(request.password(), hashToVerify);

        if(user == null || !passwordMatches) {

            if (user != null) {
                handleFailedLoginAttempt(user);
            }

            throw new InvalidCredentialsException(
                    "Nieprawidłowy login lub hasło"
            );
        }

        checkAccountStatus(user);

        loginAttemptRepository.save(LoginAttempt.builder()
                .user(user)
                .success(true)
                .attemptTime(Instant.now())
                .build());

        loginSessionRepository.invalidateAllActiveSessionsForUser(user);

        String smsCode = generateSmsCode();

        LoginSession session = LoginSession.builder()
                .user(user)
                .smsCode(smsCode)
                .expiresAt(Instant.now().plus(3, ChronoUnit.MINUTES))
                .isUsed(false)
                .build();

        session = loginSessionRepository.save(session);

        log.info("=== SYMULACJA WYSYŁKI SMS ===");
        log.info("Wysyłam uuid. Twój kod uuid to: {}", session.getId());
        log.info("Wysyłam SMS na numer {}: Twój kod logowania to {}", user.getPhoneNumber(), smsCode);
        log.info("=============================");

        return new LoginStep2Response(
                session.getId(),
                "PROVIDE_SMS_CODE",
                "Podaj kod SMS wysłany na Twój numer telefonu"
        );

    }

    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public LoginStep3Response verifyLoginStep3(LoginStep3Request request) {
        log.info("Rozpoczęto weryfikację kodu SMS dla sesji: {}", request.sessionId());

        LoginSession session = loginSessionRepository.findById(request.sessionId())
                .orElseThrow(() -> new InvalidCredentialsException("Sesja logowania nie istnieje"));

        if (Instant.now().isAfter(session.getExpiresAt())) {
            throw new InvalidCredentialsException("Czas na wpisanie kodu SMS minął. Zaloguj się ponownie.");
        }

        if (session.isUsed()) {
            throw new InvalidCredentialsException("Tem kod SMS został już wykorzystany");
        }

        if (session.getFailedAttempts() >=3) {
            session.setUsed(true);
            loginSessionRepository.save(session);
            throw new InvalidCredentialsException("Przekroczono limit prób wpisania kodu SMS. Zaloguj się ponownie.");
        }

        if (!session.getSmsCode().equals(request.smsCode())) {
            session.setFailedAttempts(session.getFailedAttempts() + 1);
            loginSessionRepository.save(session);
            throw new InvalidCredentialsException("Nieprawidłowy kod SMS.");
        }

        session.setUsed(true);
        loginSessionRepository.save(session);

        User user = session.getUser();
        if (user.getStatus() == UserStatus.PENDING) {
            user.setStatus(UserStatus.ACTIVE);
            log.info("Konto {} zostało w pełni aktywowane po pierwszym logowaniu.", user.getLogin());
        }

        log.info("Logowanie zakończone pełnym sukcesem dla: {}", user.getLogin());

        refreshTokenRepository.revokeAllUserTokens(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiresAt(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);

        return new LoginStep3Response(
                "Logowanie pomyślne",
                accessToken,
                refreshToken
        );

    }


    @Transactional(noRollbackFor = InvalidCredentialsException.class)
    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken storedToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new InvalidCredentialsException("Nieprawidłowy token odświeżania"));

        User user = storedToken.getUser();

        if (storedToken.isRevoked()) {
            log.warn("WYKRYTO POTENCJALNY ATAK! Próba użycia unieważnionego tokena dla usera: {}", user.getLogin());
            refreshTokenRepository.revokeAllUserTokens(user);
            throw new InvalidCredentialsException("Wykryto nieautoryzowane użycie tokena. Zaloguj się ponownie ze względów bezpieczeństwa.");
        }

        if (Instant.now().isAfter(storedToken.getExpiresAt())) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new InvalidCredentialsException("Token odświeżania wygasł. Zaloguj się ponownie.");
        }

        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        String newAccesToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        RefreshToken newRefreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(newRefreshToken)
                .expiresAt(Instant.now().plusMillis(refreshExpiration))
                .revoked(false)
                .build();

        refreshTokenRepository.save(newRefreshTokenEntity);

        log.info("Pomyślnie odświeżono tokeny dla usera: {}", user.getLogin());

        return new RefreshTokenResponse(newAccesToken, newRefreshToken);
    }

    public void logout(LogoutRequest request) {
        log.info("Rozpoczęto procedurę wylogowania.");

        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                    log.info("Token unieważniony. Użytkownik {} wylogowany z tego urządzenia.", token.getUser().getLogin());
                });
    }


    //Metody używane do logowania oraz rejestracji (pomocznicze)

    private String generateSmsCode() {
        int code = 100000 + SECURE_RANDOM.nextInt(900000);
        return String.valueOf(code);
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

    private void checkAccountStatus(User user) {
        if (user.getStatus() == UserStatus.BLOCKED) {
            throw new AccountBlockedException("Konto jest zablokowane. Skontaktuj się z infolinią.");
        }

        if (user.getLockedUntil() != null) {
            if (Instant.now().isBefore(user.getLockedUntil())) {
                throw new AccountBlockedException("Konto jest tymczasowo zablokowane.");
            }
            user.setLockedUntil(null);
        }
    }

    private void handleFailedLoginAttempt(User user) {
        if (user.getStatus() == UserStatus.BLOCKED) {
            return;
        }

        loginAttemptRepository.save(LoginAttempt.builder()
                .user(user)
                .success(false)
                .attemptTime(Instant.now())
                .build());

        Instant last24hours = Instant.now().minus(24, ChronoUnit.HOURS);
        int attempts = loginAttemptRepository.countByUserAndSuccessFalseAndAttemptTimeAfter(user, last24hours);

        if (attempts >= 6) {
            user.setLockedUntil(Instant.now().plus(24, ChronoUnit.HOURS));
            log.warn("Konto {} zostało zablokowane na 24h ({} błędów).", user.getLogin(), attempts);
        } else if (attempts >= 3) {
            user.setLockedUntil(Instant.now().plus(15, ChronoUnit.MINUTES));
            log.warn("Konto {} zostało zablokowane na 15 min ({} błędów).", user.getLogin(), attempts);
        } else {
            log.warn("Błędne hasło dla konta {}. Próba {}/3 do pierwszej blokady.", user.getLogin(), attempts);
        }

    }

}

// ===================================================================================
// TODO (Architektura & Rozwój mikroserwisu - do wdrożenia w późniejszych etapach):
// ===================================================================================
// 1. [Rozbicie Serwisów] Wydzielenie metody `register` do osobnego mikroserwisu
//    (np. user-service). Docelowo AuthService ma odpowiadać WYŁĄCZNIE za weryfikację
//    tożsamości, hasła, tokeny i blokady.
//
// 2. [Security Audit] Zastąpienie obecnych logów (log.warn) asynchronicznym wysyłaniem
//    zdarzeń (np. za pomocą Kafka / RabbitMQ) do dedykowanego 'audit-service'.
//    Wymagane do pełnej zgodności z audytami bezpieczeństwa (rejestrowanie LOGIN_FAILED).
//
// 3. [Device Fingerprinting] Wzbogacenie encji LoginAttempt o zapisywanie nagłówków
//    z żądania HTTP (np. User-Agent), które w przyszłości przekaże nam kontroler.
//
// 4. [Security] Login Throttling - mechanizm celowego opóźniania odpowiedzi (np. za pomocą algorytmu Token Bucket) przy kolejnych błędnych próbach logowania, aby spowolnić ataki słownikowe.
// 5. [Security] Risk-Based Authentication - analiza kontekstu logowania (nowy adres IP, inne urządzenie, dziwne godziny) w celu podniesienia poziomu weryfikacji przed wysłaniem SMS.

// UWAGA ARCHITEKTONICZNA:
// Zabezpieczenia sieciowe (Rate Limiting, ochrona przed atakami DDoS, blokowanie po IP)
// są celowo pominięte w tym kodzie. Zgodnie z architekturą mikroserwisów, za odrzucanie
// spamu przed dotarciem do AuthService odpowiada API Gateway.
// ===================================================================================
