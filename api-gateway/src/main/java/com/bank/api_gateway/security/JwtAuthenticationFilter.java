package com.bank.api_gateway.security;

import com.bank.api_gateway.dto.GatewayErrorResponse;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class JwtAuthenticationFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final JwtUtil jwtUtil;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        String authHeader = request.headers().firstHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ServerResponse.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);

        try {
            Claims claims = jwtUtil.validateTokenAndGetClaims(token);

            String correlationId = java.util.UUID.randomUUID().toString();

            ServerRequest mutatedRequest = ServerRequest.from(request)
                    .header("X-User-Login", claims.getSubject())
                    .header("X-User-Role", claims.get("role", String.class))
                    .header("X-Correlation-ID", correlationId)
                    .build();

            return next.handle(mutatedRequest);

        } catch (Exception e) {
            log.info("=== BŁĄD WERYFIKACJI JWT === : " + e.getMessage());

            String path = request.uri().getPath();

            GatewayErrorResponse errorResponse = GatewayErrorResponse.of(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "Odmowa dostępu. Powód: " + e.getMessage(),
                    path
            );

            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
