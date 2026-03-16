package com.bank.api_gateway.security;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.HandlerFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class SecurityHeadersFilter implements HandlerFilterFunction<ServerResponse, ServerResponse> {

    @Override
    public ServerResponse filter(ServerRequest request, HandlerFunction<ServerResponse> next) throws Exception {

        ServerResponse response = next.handle(request);

        return ServerResponse.from(response)
                .header("X-Frame-Options", "DENY")
                .header("X-XSS-Protection", "1; mode=block")
                .header("X-Content-Type-Options", "nosniff")
                .header("Strict-Transport-Security", "max-age=31536000; includeSubDomains")
                .header("Content-Security-Policy", "default-src 'self'")
                .build();
    }
}
