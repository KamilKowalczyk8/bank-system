package com.bank.common.onboarding_service.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;


@Component
public class InternalJwtProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String generateInternalToken() {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        return Jwts.builder()
                .subject("onboarding-service")
                .issuer("bank-system")
                .claim("role", "INTERNAL_SERVICE")
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(key)
                .compact();
    }
}
