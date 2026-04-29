package com.bank.common.api_gateway;

import com.bank.common.api_gateway.security.JwtAuthenticationFilter;
import com.bank.common.api_gateway.security.RateLimitingFilter;
import com.bank.common.api_gateway.security.SecurityHeadersFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


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
    public RouteLocator gatewayRoutes(
            RouteLocatorBuilder builder,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            SecurityHeadersFilter securityHeadersFilter,
            RateLimitingFilter rateLimitingFilter
    ) {
        return builder.routes()

                .route("auth-service-route", r -> r
                        .path("/auth/**")
                        .filters(f -> f
                                .filter(rateLimitingFilter)
                        )
                        .uri(authServiceUrl)
                )

                .route("customer-service-route", r -> r
                        .path("/api/customers/**")
                        .filters(f -> f
                                .filter(jwtAuthenticationFilter)
                                .circuitBreaker(config -> config
                                        .setName("customerServiceCB")
                                        .setFallbackUri("forward:/fallback/customer")
                                )
                        )
                        .uri(customerServiceUrl)
                )

                .route("onboarding-service-route", r -> r
                        .path("/api/onboarding/**")
                        .filters(f -> f
                                .filter(rateLimitingFilter)

                        )
                        .uri(onboardingServiceUrl)
                )

                .route("card-service-route", r -> r
                        .path("/api/cards/**")
                        .filters(f -> f
                                .filter(rateLimitingFilter)
                                .filter(jwtAuthenticationFilter)
                        )
                        .uri(cardServiceUrl)
                )

                .route("account-service-route", r -> r
                        .path("/api/accounts/**")
                        .filters(f -> f
                                .filter(rateLimitingFilter)
                                .filter(jwtAuthenticationFilter)
                        )
                        .uri(accountServiceUrl)
                )

                .route("fraud-service-route", r -> r
                        .path("/api/fraud/**")
                        .filters(f -> f
                                .filter(rateLimitingFilter)
                                .filter(jwtAuthenticationFilter)
                        )
                        .uri(fraudServiceUrl)
                )

                .route("payment-service-route", r -> r
                        .path("/api/payments/**")
                        .filters(f -> f
                                .filter(rateLimitingFilter)
                                .filter(jwtAuthenticationFilter)
                        )
                        .uri(paymentServiceUrl)
                )

                .build();
    }
}

/* * TODO: [SERVICE DISCOVERY - DOCKER / KUBERNETES]
 * 1. Obecnie środowisko deweloperskie opiera się na Docker Compose.
 * Service Discovery jest realizowane natywnie przez Docker DNS (np. http://account-service:8084).
 * 2. DOCELOWE ROZWIĄZANIE PROD: Kubernetes (K8s).
 * Po migracji na K8s, adresy zostaną uproszczone do natywnych serwisów k8s (np. http://account-service),
 * a Load Balancing przejmie Ingress/Service z Kubernetesa.
 */
