package com.banking.accountservice.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AuthenticationFilterConfig extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Allow unauthenticated access to OpenAPI and Swagger endpoints in dev profile
        String path = request.getRequestURI();
        boolean isDev = activeProfile != null && activeProfile.contains("dev");
        if (isDev && (path.startsWith("/v3/api-docs") || path.startsWith("/swagger-ui") || path.startsWith("/swagger-ui.html") || path.startsWith("/webjars/"))) {
            filterChain.doFilter(request, response);
            return;
        }

        final String authHeader = request.getHeader("Authorization");
        final String jwt;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            if (jwtUtils.validateToken(jwt)) {
                Claims claims = jwtUtils.extractAllClaims(jwt);

                CurrentUser currentUser = CurrentUser.builder()
                        .subject(claims.getSubject())
                        .userId(claims.get("userId", Long.class))
                        .role(claims.get("role", String.class))
                        .firstName(claims.get("firstName", String.class))
                        .lastName(claims.get("lastName", String.class))
                        .build();

                String userEmail = claims.getSubject();

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    String role = claims.get("role", String.class);
                    List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                            new SimpleGrantedAuthority(role)
                    );

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            currentUser,
                            null,
                            authorities
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT Authentication failed: {}", e);
        }

        filterChain.doFilter(request, response);
    }
}
