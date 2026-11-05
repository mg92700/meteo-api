package com.meto.api.infrastructure.config;

import com.meto.api.domain.service.AlerteDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour les beans du domaine.
 * Cette approche respecte les principes de clean architecture en déclarant les beans
 * du domaine dans la couche infrastructure plutôt que d'utiliser des annotations
 * directement dans le domaine.
 */
@Configuration
public class DomainBeanConfig {

    @Bean
    public AlerteDomainService alerteDomainService() {
        return new AlerteDomainService();
    }
}

