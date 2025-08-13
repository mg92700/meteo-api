package com.meto.api.services;


import com.meto.api.mapper.AlerteMapper;
import com.meto.api.model.AlerteEntity;
import com.meto.api.pojo.Alerte;
import com.meto.api.repository.AlerteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlerteService {

    private final AlerteRepository alerteRepository;

    private final AlerteMapper alerteMapper;

    public AlerteService(AlerteRepository alerteRepository, AlerteMapper alerteMapper) {
        this.alerteRepository = alerteRepository;
        this.alerteMapper = alerteMapper;
    }

    public List<Alerte> getAlertesActivesParVille(String ville) {
        List<AlerteEntity> alertesEntities = alerteRepository.findByVilleAndActiveTrue(ville);
        return alertesEntities.stream()
                .map(alerteMapper::toAlerte)  // Utilisation du mapper pour convertir AlerteEntity en Alerte
                .collect(Collectors.toList());
    }

}
