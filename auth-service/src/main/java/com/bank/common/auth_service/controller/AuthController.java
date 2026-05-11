package com.bank.common.auth_service.controller;

import com.bank.common.auth_service.dto.*;
import com.bank.common.auth_service.dto.login.*;
import com.bank.common.auth_service.dto.register.RegisterRequest;
import com.bank.common.auth_service.dto.register.RegisterResponse;
import com.bank.common.auth_service.dto.token.RefreshTokenRequest;
import com.bank.common.auth_service.dto.token.RefreshTokenResponse;
import com.bank.common.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpointy do zarzadzania rejestracja i wieloetapowym logowaniem w systemie bankowym")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Rejestracja nowego klienta", description = "Tworzy nowe konto, generuje 8-cyfrowy NIK oraz wysyła hasło tymczasowe.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Konto zostało pomyślnie utworzone"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji danych wejściowych (np. zły PESEL lub pusty numer telefonu)")
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterRequest request) {
        RegisterResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @Operation(summary = "Awaryjne usuwanie konta (Saga Rollback)", description = "Fizycznie usuwa konto z bazy danych na podstawie podanego loginu. Endpoint przeznaczony do wycofywania transakcji przez Onboarding-Service w przypadku niepowodzenia rejestracji.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Konto zostało pomyślnie usunięte (No Content)"),
            @ApiResponse(responseCode = "404", description = "Nie znaleziono konta do usunięcia") // Ten błąd dorzucimy, gdy napiszesz w auth-service GlobalExceptionHandler!
    })
    @DeleteMapping("/{authId}")
    public ResponseEntity<Void> deleteAccountHard(@PathVariable String authId) {
        authService.deleteAccountHard(authId);
        return ResponseEntity.noContent().build();
    }


    @Operation(summary = "Logowanie - Krok 1 (Weryfikacja NIK)", description = "Weryfikuje poprawność formatu loginu i przygotowuje proces logowania. Chroni przed enumeracją użytkowników.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zwraca instrukcję do kolejnego kroku (zazwyczaj PROVIDE_PASSWORD)"),
            @ApiResponse(responseCode = "400", description = "Brak loginu w żądaniu")
    })
    @PostMapping("/login/step1")
    public ResponseEntity<LoginStep1Response> loginStep1(@RequestBody @Valid LoginStep1Request request) {
        LoginStep1Response response = authService.verifyLoginStep1(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logowanie - Krok 2 (Weryfikacja hasła)", description = "Weryfikuje hasło użytkownika z wykorzystaniem Argon2. Obsługuje mechanizm blokady konta po 3 nieudanych próbach.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hasło poprawne, zwraca instrukcję do kroku z kodem SMS"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji (np. puste hasło)"),
            @ApiResponse(responseCode = "401", description = "Nieprawidłowy login lub hasło"),
            @ApiResponse(responseCode = "403", description = "Konto zostało zablokowane (trwale lub czasowo)")
    })
    @PostMapping("login/step2")
    public ResponseEntity<LoginStep2Response> loginStep2(@RequestBody @Valid LoginStep2Request request) {
        LoginStep2Response response = authService.verifyLoginStep2(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Logowanie - Krok 3 (Weryfikacja kodu SMS)", description = "Weryfikuje kod SMS i identyfikator sesji. Wydaje ostateczny token JWT.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Kod poprawny, logowanie udane"),
            @ApiResponse(responseCode = "401", description = "Nieprawidłowy kod, wygasła lub zużyta sesja")
    })
    @PostMapping("/login/step3")
    public ResponseEntity<LoginStep3Response> loginStep3(@RequestBody @Valid LoginStep3Request request) {
        LoginStep3Response response = authService.verifyLoginStep3(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Ustawienie pierwszego, stałego hasła",
            description = "Pozwala na zmianę hasła tymczasowego na stałe. Wymaga autoryzacji ograniczonym tokenem. Zwraca docelową parę tokenów (Access i Refresh), odblokowując pełny dostęp do banku."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Hasło zmienione pomyślnie, zwraca pełne tokeny JWT"),
            @ApiResponse(responseCode = "400", description = "Błąd walidacji danych (np. hasło nie spełnia rygorystycznych wymagań regex)"),
            @ApiResponse(responseCode = "401", description = "Brak autoryzacji lub token wygasł"),
            @ApiResponse(responseCode = "403", description = "Odmowa dostępu: brak zweryfikowanego telefonu lub konto ma już stałe hasło")
    })
    @PostMapping("/first-password-setup")
    public ResponseEntity<FirstPasswordSetupResponse> setupFirstPassword(
            @RequestBody @Valid FirstPasswordSetupRequest request,
            Principal principal
    ) {
        String login = principal.getName();
        FirstPasswordSetupResponse response = authService.setupFirstPassword(login, request);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Odświeżanie sesji (Refresh Token Rotation)", description = "Wymienia ważny Refresh Token na nową parę tokenów (Access i Refresh). Automatycznie unieważnia stary token. Zabezpiecza przed Replay Attack.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Zwraca nową parę tokenów JWT"),
            @ApiResponse(responseCode = "401", description = "Token odświeżania wygasł, jest nieprawidłowy lub został skradziony (revoked)")
    })
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Wylogowywanie (Kill Switch)", description = "Bezpiecznie unieważnia (pali) podany Refresh Token w bazie danych. Operacja idempotentna.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Operacja zakończona sukcesem (nawet jeśli podany token nie istniał)")
    })
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody @Valid LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }

}

