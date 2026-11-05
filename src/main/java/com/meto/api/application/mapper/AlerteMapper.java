package com.meto.api.application.mapper;

import com.meto.api.application.dto.AlerteDto;
import com.meto.api.domain.model.Alerte;
import com.meto.api.infrastructure.adapter.persistence.entity.AlerteEntity;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper pour convertir entre les entités Alerte (domaine) et AlerteDto (application).
 * Cette classe n'a pas de dépendances Spring et reste testable indépendamment.
 */
public class AlerteMapper {

    public AlerteDto toDto(Alerte alerte) {
        if (alerte == null) {
            return null;
        }
        return new AlerteDto(
            alerte.getVille(),
            alerte.getSeuilTemperature(),
            alerte.getSeuilHumidite(),
            alerte.getDureeActivationJours(),
            alerte.isActive(),
            alerte.getDateCreation(),
            alerte.getUtilisateurId()
        );
    }

    public Alerte toAlerte(AlerteDto dto) {
        if (dto == null) {
            return null;
        }
        Alerte alerte = new Alerte();
        alerte.setVille(dto.ville());
        alerte.setSeuilTemperature(dto.seuilTemperature());
        alerte.setSeuilHumidite(dto.seuilHumidite());
        alerte.setDureeActivationJours(dto.dureeActivationJours());
        alerte.setActive(dto.active());
        alerte.setDateCreation(dto.dateCreation());
        alerte.setUtilisateurId(dto.utilisateurId());
        return alerte;
    }

    public Alerte toAlerte(AlerteEntity alerteEntity) {
        if (alerteEntity == null) {
            return null;
        }
        Alerte alerte = new Alerte();

        alerte.setVille(alerteEntity.getVille());
        alerte.setSeuilTemperature(alerteEntity.getSeuilTemperature());
        alerte.setSeuilHumidite(alerteEntity.getSeuilHumidite());
        alerte.setDureeActivationJours(alerteEntity.getDureeActivationJours());
        alerte.setActive(alerteEntity.isActive());
        alerte.setDateCreation(alerteEntity.getDateCreation());
        alerte.setUtilisateurId(alerteEntity.getUser().getId());
        return alerte;
    }

    public List<Alerte> toAlertes(List<AlerteEntity> alerteEntities) {
        if (alerteEntities == null) {
            return List.of();
        }
        return alerteEntities.stream()
            .map(this::toAlerte)
            .collect(Collectors.toList());
    }

    public AlerteEntity toAlertEntity(Alerte alerte) {
        if (alerte == null) {
            return null;
        }
        AlerteEntity entity = new AlerteEntity();
        entity.setVille(alerte.getVille());
        entity.setSeuilTemperature(alerte.getSeuilTemperature());
        entity.setSeuilHumidite(alerte.getSeuilHumidite());
        entity.setDureeActivationJours(alerte.getDureeActivationJours());
        entity.setActive(alerte.isActive());
        entity.setDateCreation(alerte.getDateCreation());
        return entity;
    }
}

