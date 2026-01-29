package com.banking.apigateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.function.HandlerFilterFunction;
import org.springframework.web.servlet.function.ServerResponse;

@Component
public class AuthenticationFilter {

    private final JwtUtils jwtUtils;

    public AuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    public HandlerFilterFunction<ServerResponse, ServerResponse> apply() {
        return (request, next) -> {
            // 1. Extract Header
            String authHeader = request.headers().firstHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                        .body("Missing or Invalid Authorization Header");
            }

            try {
                // 2. Validate JWT
                String token = authHeader.substring(7);
                jwtUtils.validateToken(token);

                // 3. Continue to the microservice
                return next.handle(request);
            } catch (Exception e) {
                return ServerResponse.status(HttpStatus.UNAUTHORIZED)
                        .body("Unauthorized: " + e.getMessage());
            }
        };
    }
}