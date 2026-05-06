package br.com.alura.booking.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "Booking Service API",
        version     = "v1",
        description = "API de gerenciamento de reservas de salas"
    )
)
@SecurityScheme(
    name        = "basicAuth",
    type        = SecuritySchemeType.HTTP,
    scheme      = "basic",
    description = "Use user:user123 (USER) ou admin:admin123 (ADMIN)"
)
public class OpenApiConfig {}
