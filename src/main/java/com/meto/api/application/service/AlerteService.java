package com.meto.api.application.service;


import com.meto.api.application.port.AlerteRepositoryPort;
import com.meto.api.domain.model.Alerte;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlerteService {

    private final AlerteRepositoryPort alerteRepository;

    public AlerteService(AlerteRepositoryPort alerteRepository) {
        this.alerteRepository = alerteRepository;
    }

    public List<Alerte> getAlertesActivesParVille(String ville) {
        return alerteRepository.findByVilleAndActiveTrue(ville);
    }

}
