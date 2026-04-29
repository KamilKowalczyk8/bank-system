package com.bank.common.api_gateway.security;

import com.bank.common.api.ErrorReporter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.core.Ordered;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class RateLimitingFilter implements GatewayFilter, Ordered {

    private final StringRedisTemplate redisTemplate;
    private final ErrorReporter errorReporter;

    private static final int MAX_REQUESTS_PER_MINUTE = 10;

    @Override
    public Mono<Void> filter(org.springframework.web.server.ServerWebExchange exchange,
                             org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        String ip = exchange.getRequest()
                .getRemoteAddress()
                .getAddress()
                .getHostAddress();

        String key = "rate_limit:" + ip;

        String countStr = redisTemplate.opsForValue().get(key);
        int count = countStr != null ? Integer.parseInt(countStr) : 0;

        if (count >= MAX_REQUESTS_PER_MINUTE) {

            String msg = "DDoS attempt from IP: " + ip;

            log.warn(msg);
            errorReporter.report(new SecurityException(msg));

            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);

            return exchange.getResponse().setComplete();
        }

        redisTemplate.opsForValue().increment(key);

        if (count == 0) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -100;
    }
}