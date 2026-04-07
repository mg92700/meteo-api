package com.meto.api.application.service;

import com.meto.api.application.port.AlerteRepositoryPort;
import com.meto.api.domain.model.Alerte;

import java.util.List;

/**
 * Facade applicative pour l'accès aux alertes.
 * Cette classe est une interface de haut niveau destinée à la couche présentation.
 * Elle coordonne les Use Cases et la présentation des données.
 *
 * Note: Cette classe sera instanciée et configurée par la couche infrastructure.
 */
public class AlerteApplicationService {

    private final AlerteRepositoryPort alerteRepository;

    public AlerteApplicationService(AlerteRepositoryPort alerteRepository) {
        this.alerteRepository = alerteRepository;
    }

    public List<Alerte> getAlertesActivesParVille(String ville) {
        return alerteRepository.findByVilleAndActiveTrue(ville);
    }

}
