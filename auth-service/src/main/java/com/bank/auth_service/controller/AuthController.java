package com.bank.auth_service.controller;

import com.bank.auth_service.dto.*;
import com.bank.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpointy do zarządzania rejestracją i wieloetapowym logowaniem w systemie bankowym")
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


}

