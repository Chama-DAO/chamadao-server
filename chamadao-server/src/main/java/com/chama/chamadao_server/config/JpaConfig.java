package com.chama.chamadao_server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Configuration class to enable JPA auditing
 * This enables automatic population of @CreatedDate and @LastModifiedDate fields
 */
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}