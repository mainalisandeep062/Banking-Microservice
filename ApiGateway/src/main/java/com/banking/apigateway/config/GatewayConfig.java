package com.banking.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;
import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;
import static org.springframework.cloud.gateway.server.mvc.predicate.GatewayRequestPredicates.path;

@Configuration
public class GatewayConfig {

    @Bean
    public RouterFunction<ServerResponse> gatewayRoutes(AuthenticationFilter authFilter) {
        return route()
                // 1. PUBLIC ROUTE: User Service
                .route(path("/api/auth/**"), http())
                .filter(lb("USER-SERVICE"))
                .build()

                // 2. SECURED USER ROUTES (Profile, Update, etc.)
                .and(route()
                        .route(path("/api/user/**"), http())
                        .filter(authFilter.apply())
                        .filter(lb("USER-SERVICE"))
                        .build())

                // 3. SECURED ROUTE: Account Service
                .and(route()
                        .route(path("/api/account/**"), http())
                        .filter(authFilter.apply())   // Your JWT Bouncer
                        .filter(lb("ACCOUNT-SERVICE")) // Load balancer
                        .build())

                // 4. SECURED ROUTE: Transaction Service
                .and(route()
                        .route(path("/api/transaction/**"), http())
                        .filter(authFilter.apply())
                        .filter(lb("TRANSACTION-SERVICE"))
                        .build())

                // 5. SECURED ROUTE: Notification Service
                .and(route()
                        .route(path("/api/notification/**"), http())
                        .filter(authFilter.apply())
                        .filter(lb("NOTIFICATION-SERVICE"))
                        .build());
    }
}