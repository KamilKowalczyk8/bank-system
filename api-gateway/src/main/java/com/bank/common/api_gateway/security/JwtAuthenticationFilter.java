package com.bank.common.api_gateway.security;

import com.bank.common.api_gateway.dto.GatewayErrorResponse;
import io.jsonwebtoken.Claims;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import com.bank.common.api.ErrorReporter;

@Component
public class JwtAuthenticationFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    private final JwtUtil jwtUtil;
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final ErrorReporter errorReporter;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, ErrorReporter errorReporter) {
        this.jwtUtil = jwtUtil;
        this.errorReporter = errorReporter;
    }

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {
        String authHeader = request.headers().firstHeader(HttpHeaders.AUTHORIZATION);
        String path = request.uri().getPath();

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            String msg = "Próba nieautoryzowanego dostępu (brak tokenu Bearer) do ścieżki: " + path;
            log.warn(msg);
            errorReporter.report(new SecurityException(msg));

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
            String msg = "Nieudana weryfikacja tokenu JWT dla ścieżki : " + path + ". Powód: " + e.getMessage();
            log.warn(msg);
            errorReporter.report(new SecurityException(msg));

            GatewayErrorResponse errorResponse = GatewayErrorResponse.of(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "Odmowa dostępu. Przez to że twój token wygasł lub jest niezatwierdzony ",
                    path
            );

            return ServerResponse.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
