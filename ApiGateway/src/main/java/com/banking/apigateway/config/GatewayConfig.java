package com.banking.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.stripPrefix;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(AuthenticationFilter authFilter) {
        return
                // 1. SWAGGER/OPENAPI ROUTES (No auth, with stripPrefix for docs/UI)
                // User Service Swagger
                route("user-swagger-route")
                        .route(path("/api/user/v3/api-docs/**").or(path("/api/user/swagger-ui/**")), http())
                        .before(stripPrefix(2))  // Strips /api/user -> forwards as /v3/api-docs or /swagger-ui/**
                        .filter(lb("USER-SERVICE"))
                        .build()
                        .and(route("account-swagger-route")
                                .route(path("/api/account/v3/api-docs/**").or(path("/api/account/swagger-ui/**")), http())
                                .before(stripPrefix(2))
                                .filter(lb("ACCOUNT-SERVICE"))
                                .build())
                        .and(route("transaction-swagger-route")
                                .route(path("/api/transaction/v3/api-docs/**").or(path("/api/transaction/swagger-ui/**")), http())
                                .before(stripPrefix(2))
                                .filter(lb("TRANSACTION-SERVICE"))
                                .build())
                        .and(route("notification-swagger-route")
                                .route(path("/api/notification/v3/api-docs/**").or(path("/api/notification/swagger-ui/**")), http())
                                .before(stripPrefix(2))
                                .filter(lb("NOTIFICATION-SERVICE"))
                                .build())

                        // 2. EXISTING API ROUTES (Unchanged, placed after for lower priority)
                        .and(route()
                                // PUBLIC ROUTE: User Service Auth
                                .route(path("/api/auth/**"), http())
                                .filter(lb("USER-SERVICE"))
                                .build())
                        .and(route()
                                // SECURED USER ROUTES (Profile, Update, etc.)
                                .route(path("/api/user/**"), http())
                                .filter(authFilter.apply())
                                .filter(lb("USER-SERVICE"))
                                .build())
                        .and(route()
                                // SECURED ROUTE: Account Service
                                .route(path("/api/account/**"), http())
                                .filter(authFilter.apply())
                                .filter(lb("ACCOUNT-SERVICE"))
                                .build())
                        .and(route()
                                // SECURED ROUTE: Transaction Service
                                .route(path("/api/transaction/**"), http())
                                .filter(authFilter.apply())
                                .filter(lb("TRANSACTION-SERVICE"))
                                .build())
                        .and(route()
                                // SECURED ROUTE: Notification Service
                                .route(path("/api/notification/**"), http())
                                .filter(authFilter.apply())
                                .filter(lb("NOTIFICATION-SERVICE"))
                                .build());
    }
}