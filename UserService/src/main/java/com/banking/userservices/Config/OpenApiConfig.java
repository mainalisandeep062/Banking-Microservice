package com.banking.userservices.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import org.springframework.context.annotation.Configuration;

@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
@OpenAPIDefinition(
        info = @Info(title = "${openapi.service.title:User Service}", version = "${openapi.service.version:1.0.0}", description = "${openapi.service.description:User service API}"),
        security = @SecurityRequirement(name = "bearerAuth")
)
@Configuration
public class OpenApiConfig {
    // Intentionally empty - annotations configure the OpenAPI security and metadata
}
