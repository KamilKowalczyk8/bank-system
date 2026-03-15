package com.bank.api_gateway;

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
    public RouterFunction<ServerResponse> gatewayRoutes() {
        return route("auth-service-route")
                        .POST("/auth/**", http())
                        .before(uri("http://localhost:8081"))
                .build()
                .and(route("customer-service-route")
                        .POST("/api/customers/**", http())
                        .before(uri("http://localhost:8082"))
                .build())
                .and(route("onboarding-service-route")
                        .POST("/api/onboarding/**", http())
                        .before(uri("http://localhost:8083"))
                .build());
    }
}
