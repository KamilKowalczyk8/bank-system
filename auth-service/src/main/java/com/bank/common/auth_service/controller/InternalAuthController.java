package com.bank.common.auth_service.controller;

import com.bank.common.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
@RequestMapping("/internal/auth")
@RequiredArgsConstructor
@Slf4j
public class InternalAuthController {

    private final AuthService authService;

    @DeleteMapping("/{authId}")
    public ResponseEntity<Void> deleteAccountHard(@PathVariable String authId) {
        authService.deleteAccountHard(authId);
        return ResponseEntity.noContent().build();
    }
}
