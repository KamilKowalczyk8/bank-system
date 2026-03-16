package com.bank.api_gateway;

import com.bank.api_gateway.security.JwtAuthenticationFilter;
import com.bank.api_gateway.security.RateLimitingFilter;
import com.bank.api_gateway.security.SecurityHeadersFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;

@Configuration
public class GatewayRoutingConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            SecurityHeadersFilter securityHeadersFilter,
            RateLimitingFilter rateLimitingFilter
    ) {
        RouterFunction<ServerResponse> routes = route("auth-service-route")
                .POST("/auth/**", http())
                .before(uri("http://localhost:8081"))
                .build()
                .and(route("customer-service-route")
                        .GET("/api/customers/**", http())
                        .before(uri("http://localhost:8082"))
                        .filter(jwtAuthenticationFilter)
                        .build())
                .and(route("onboarding-service-route")
                        .POST("/api/onboarding/**", http())
                        .before(uri("http://localhost:8083"))
                        .build());

        return routes
                .filter(rateLimitingFilter)
                .filter(securityHeadersFilter);


    }
}
