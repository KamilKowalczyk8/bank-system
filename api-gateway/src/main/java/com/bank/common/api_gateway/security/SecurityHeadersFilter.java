package com.bank.common.api_gateway.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class SecurityHeadersFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        exchange.getResponse().getHeaders().add("X-Frame-Options", "DENY");
        exchange.getResponse().getHeaders().add("X-Content-Type-Options", "nosniff");
        exchange.getResponse().getHeaders().add("Strict-Transport-Security",
                "max-age=31536000; includeSubDomains");
        exchange.getResponse().getHeaders().add("Content-Security-Policy", "default-src 'self'");
        exchange.getResponse().getHeaders().add("Referrer-Policy", "no-referrer");
        exchange.getResponse().getHeaders().add("Permissions-Policy",
                "geolocation=(), microphone=(), camera=()");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -120;
    }
}