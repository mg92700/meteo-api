package com.meto.api.infrastructure.config;

import com.meto.api.application.mapper.AlerteMapper;
import com.meto.api.application.port.AlerteRepositoryPort;
import com.meto.api.application.service.AlerteApplicationService;
import com.meto.api.application.usecase.GetAlertesActivesParVilleUseCase;
import com.meto.api.domain.service.AlerteDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration pour les beans de la couche Application.
 * Cette configuration enregistre les Use Cases, Services applicatifs et Mappers
 * selon les principes de clean architecture.
 *
 * - Les Use Cases sont des instances sans état représentant les cas d'utilisation métier
 * - Les Services applicatifs coordonnent les interactions entre use cases et ports
 * - Les Mappers convertissent entre les différentes représentations des entités
 */
@Configuration
public class ApplicationBeanConfig {

    /**
     * Use Case: Récupérer les alertes actives par ville
     */
    @Bean
    public GetAlertesActivesParVilleUseCase getAlertesActivesParVilleUseCase(
            AlerteRepositoryPort alerteRepository) {
        return new GetAlertesActivesParVilleUseCase(alerteRepository);
    }

    /**
     * Service applicatif pour les opérations sur les alertes
     */
    @Bean
    public AlerteApplicationService alerteApplicationService(
            AlerteRepositoryPort alerteRepository) {
        return new AlerteApplicationService(alerteRepository);
    }

    /**
     * Mapper pour convertir entre Alerte (domaine) et AlerteDto (application)
     */
    @Bean
    public AlerteMapper alerteMapper() {
        return new AlerteMapper();
    }
}

