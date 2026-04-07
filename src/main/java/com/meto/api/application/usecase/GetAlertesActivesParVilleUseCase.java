package com.meto.api.application.usecase;

import com.meto.api.application.port.AlerteRepositoryPort;
import com.meto.api.domain.model.Alerte;

import java.util.List;

/**
 * Use Case pour récupérer les alertes actives par ville.
 * Cette classe représente un cas d'utilisation métier et ne dépend pas de Spring.
 */
public class GetAlertesActivesParVilleUseCase {

    private final AlerteRepositoryPort alerteRepository;

    public GetAlertesActivesParVilleUseCase(AlerteRepositoryPort alerteRepository) {
        this.alerteRepository = alerteRepository;
    }

    public List<Alerte> execute(String ville) {
        return alerteRepository.findByVilleAndActiveTrue(ville);
    }
}

