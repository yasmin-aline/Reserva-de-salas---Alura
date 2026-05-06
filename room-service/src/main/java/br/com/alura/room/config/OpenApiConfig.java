package br.com.alura.room.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "Room Service API",
        version     = "v1",
        description = "API de gerenciamento de salas de reuniao"
    )
)
@SecurityScheme(
    name        = "basicAuth",
    type        = SecuritySchemeType.HTTP,
    scheme      = "basic",
    description = "Use admin:admin123 para operacoes de escrita"
)
public class OpenApiConfig {}

