package com.bank.common.api_gateway;

import com.bank.common.api_gateway.security.JwtAuthenticationFilter;
import com.bank.common.api_gateway.security.RateLimitingFilter;
import com.bank.common.api_gateway.security.SecurityHeadersFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions.circuitBreaker;

@Configuration
public class GatewayRoutingConfig {

    @Value("${services.auth.url}")
    private String authServiceUrl;

    @Value("${services.customer.url}")
    private String customerServiceUrl;

    @Value("${services.onboarding.url}")
    private String onboardingServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            SecurityHeadersFilter securityHeadersFilter,
            RateLimitingFilter rateLimitingFilter
    ) {
        RouterFunction<ServerResponse> routes = route("auth-service-route")
                .POST("/auth/**", http())
                .before(uri(authServiceUrl))
                .build()
                .and(route("customer-service-route")
                        .GET("/api/customers/**", http())
                        .before(uri(customerServiceUrl))
                        .filter(jwtAuthenticationFilter)
                        .filter(circuitBreaker("customerServiceCB", java.net.URI.create("forward:/fallback/customer")))
                        .build())
                .and(route("onboarding-service-route")
                        .POST("/api/onboarding/**", http())
                        .before(uri(onboardingServiceUrl))
                        .build());

        return routes
                .filter(rateLimitingFilter)
                .filter(securityHeadersFilter);
    }
}

/* * TODO: [DŁUG TECHNOLOGICZNY - SERVICE DISCOVERY]
 * 1. Obecnie adresy mikroserwisów (np. http://localhost:8081) są "zahardkodowane" na sztywno.
 * W środowisku produkcyjnym adresy IP i porty zmieniają się dynamicznie (np. w Dockerze/Kubernetesie).
 * 2. DOCELOWE ROZWIĄZANIE: Wdrożyć wzorzec Service Discovery (np. Netflix Eureka Server).
 * Gdy Eureka będzie gotowa, zamienimy sztywne adresy na nazwy serwisów, np.:
 * z: .before(uri("http://localhost:8081"))
 * na: .before(uri("lb://auth-service")) // lb = Load Balancer
 * 3. Do tego czasu pamiętać o ręcznej zmianie portów w razie przenosin na inny serwer.
 */
