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
import static org.springframework.web.servlet.function.RequestPredicates.path;

@Configuration
public class GatewayRoutingConfig {

    @Value("${services.auth.url}")
    private String authServiceUrl;

    @Value("${services.customer.url}")
    private String customerServiceUrl;

    @Value("${services.onboarding.url}")
    private String onboardingServiceUrl;

    @Value("${services.card.url}")
    private String cardServiceUrl;

    @Value("${services.account.url}")
    private String accountServiceUrl;

    @Value("${services.payment.url}")
    private String paymentServiceUrl;

    @Value("${services.fraud.url}")
    private String fraudServiceUrl;

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            SecurityHeadersFilter securityHeadersFilter,
            RateLimitingFilter rateLimitingFilter
    ) {
        RouterFunction<ServerResponse> routes = route("auth-service-route")
                .route(path("/auth/**"), http())
                .before(uri(authServiceUrl))
                .build()
                .and(route("customer-service-route")
                        .route(path("/api/customers/**"), http())
                        .before(uri(customerServiceUrl))
                        .filter(jwtAuthenticationFilter)
                        .filter(circuitBreaker("customerServiceCB", java.net.URI.create("forward:/fallback/customer")))
                        .build())
                .and(route("onboarding-service-route")
                        .route(path("/api/onboarding/**"), http())
                        .before(uri(onboardingServiceUrl))
                        .build())
                .and(route("card-service-route")
                        .route(path("/api/cards/**"), http())
                        .before(uri(cardServiceUrl))
                        .filter(jwtAuthenticationFilter)
                        .build())
                .and(route("account-service-route")
                        .route(path("/api/accounts/**"), http())
                        .before(uri(accountServiceUrl))
                        .filter(jwtAuthenticationFilter)
                        .build())
                .and(route("fraud-service-route")
                        .route(path("/api/fraud/**"), http())
                        .before(uri(fraudServiceUrl))
                        .filter(jwtAuthenticationFilter)
                        .build())
                .and(route("payment-service-route")
                        .route(path("/api/payments/**"), http())
                        .before(uri(paymentServiceUrl))
                        .filter(jwtAuthenticationFilter)
                        .build());

        return routes
                .filter(rateLimitingFilter)
                .filter(securityHeadersFilter);
    }
}

/* * TODO: [SERVICE DISCOVERY - DOCKER / KUBERNETES]
 * 1. Obecnie środowisko deweloperskie opiera się na Docker Compose.
 * Service Discovery jest realizowane natywnie przez Docker DNS (np. http://account-service:8084).
 * 2. DOCELOWE ROZWIĄZANIE PROD: Kubernetes (K8s).
 * Po migracji na K8s, adresy zostaną uproszczone do natywnych serwisów k8s (np. http://account-service),
 * a Load Balancing przejmie Ingress/Service z Kubernetesa.
 */
