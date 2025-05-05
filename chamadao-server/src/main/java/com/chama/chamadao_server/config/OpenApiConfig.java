package com.chama.chamadao_server.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuration class for OpenAPI documentation
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation
     * @return The OpenAPI configuration
     */
    @Bean
    public OpenAPI chamadaoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ChamaDAO API")
                        .description("API for ChamaDAO, a decentralized platform for informal savings and loaning groups (chamas) in Africa")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("ChamaDAO Team")
                                .url("https://chamadao.org")
                                .email("info@chamadao.org"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.chamadao.org")
                                .description("Production Server")
                ));
    }
}