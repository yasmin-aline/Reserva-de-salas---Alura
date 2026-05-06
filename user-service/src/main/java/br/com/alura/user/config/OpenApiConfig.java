package br.com.alura.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "User Service API",
        version     = "v1",
        description = "API de gerenciamento de usuarios e autenticacao 2FA"
    )
)
@SecurityScheme(
    name        = "basicAuth",
    type        = SecuritySchemeType.HTTP,
    scheme      = "basic",
    description = "Use admin:admin123 (todos os endpoints requerem ADMIN)"
)
public class OpenApiConfig {}

