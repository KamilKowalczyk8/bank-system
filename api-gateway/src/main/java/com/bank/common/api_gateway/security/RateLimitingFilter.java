package com.bank.common.api_gateway.security;

import com.bank.common.api.ErrorReporter;
import com.bank.common.api_gateway.dto.GatewayErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

import java.time.Duration;

@Component
@Slf4j
public class RateLimitingFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final StringRedisTemplate redisTemplate;
    private final ErrorReporter errorReporter;

    private static final int MAX_REQUESTS_PER_MINUTE = 10;

    public RateLimitingFilter(StringRedisTemplate redisTemplate, ErrorReporter errorReporter) {
        this.redisTemplate = redisTemplate;
        this.errorReporter = errorReporter;
    }

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {

        String clientIp = request.remoteAddress()
                .map(address -> address.getAddress().getHostAddress())
                .orElse("unknown-ip");

        String redisKey = "rate_limit:" + clientIp;

        String currentRequestsStr = redisTemplate.opsForValue().get(redisKey);
        int currentRequest = currentRequestsStr != null ? Integer.parseInt(currentRequestsStr) : 0;

        if (currentRequest >= MAX_REQUESTS_PER_MINUTE) {
            String msg = "ALARM SECURITY: Wykryto atak DDoS lub nadmierny ruch z adresu IP: " + clientIp;
            log.warn(msg);

            errorReporter.report(new SecurityException(msg));

            GatewayErrorResponse errorResponse = GatewayErrorResponse.of(
                    HttpStatus.TOO_MANY_REQUESTS.value(),
                    HttpStatus.TOO_MANY_REQUESTS.getReasonPhrase(),
                    "Przekroczono limit zapytań (" + MAX_REQUESTS_PER_MINUTE + "/min). Zwolnij!",
                    request.uri().getPath()
            );
            return ServerResponse.status(HttpStatus.TOO_MANY_REQUESTS).body(errorResponse);
        }

        redisTemplate.opsForValue().increment(redisKey);

        if (currentRequest == 0) {
            redisTemplate.expire(redisKey, Duration.ofMinutes(1));
        }
        return next.handle(request);
    }
}
