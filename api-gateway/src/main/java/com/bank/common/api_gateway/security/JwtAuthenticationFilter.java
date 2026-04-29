package com.bank.common.api_gateway.security;

import com.bank.common.api.ErrorReporter;
import com.bank.common.api_gateway.dto.GatewayErrorResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GatewayFilter, Ordered {

    private final JwtUtil jwtUtil;
    private final ErrorReporter errorReporter;

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String msg = "Brak tokenu Bearer dla ścieżki: " + path;

            log.warn(msg);
            errorReporter.report(new SecurityException(msg));

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateTokenAndGetClaims(token);

            String correlationId = UUID.randomUUID().toString();

            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(builder -> builder
                            .header("X-User-Login", claims.getSubject())
                            .header("X-User-Role", claims.get("role", String.class))
                            .header("X-Correlation-ID", correlationId)
                    )
                    .build();

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            String msg = "JWT invalid for path: " + path + " reason: " + e.getMessage();

            log.warn(msg);
            errorReporter.report(new SecurityException(msg));

            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);

            GatewayErrorResponse body = GatewayErrorResponse.of(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "Token wygasł lub jest niepoprawny",
                    path
            );

            // reactive write response
            return exchange.getResponse()
                    .writeWith(Mono.just(
                            exchange.getResponse()
                                    .bufferFactory()
                                    .wrap(body.toString().getBytes())
                    ));
        }
    }

    @Override
    public int getOrder() {
        return -10;
    }
}