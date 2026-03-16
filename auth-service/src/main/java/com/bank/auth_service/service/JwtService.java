package com.bank.auth_service.service;

import com.bank.auth_service.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;



    public String generateAccessToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole().name());
        extraClaims.put("token_type", "ACCESS");

        Date now = new Date();

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getLogin())
                .issuer("Bank-Auth-Service")
                .audience().add("Bank-Internal-Network").and()
                .id(java.util.UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + jwtExpiration))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("token_type", "REFRESH");

        Date now = new Date();

        return Jwts.builder()
                .claims(extraClaims)
                .subject(user.getLogin())
                .issuer("Bank-Auth-Service")
                .audience().add("Bank-Internal-Network").and()
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + refreshExpiration))
                .signWith(getSignKey(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

//TODO
//Redis Blacklist (do wylogowywania)
//RS256 (kluczach asymetrycznych)