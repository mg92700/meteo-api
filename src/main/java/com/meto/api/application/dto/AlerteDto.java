package com.meto.api.application.dto;

import java.time.LocalDateTime;

public record AlerteDto(String ville, Double seuilTemperature, Double seuilHumidite, int dureeActivationJours, boolean active, LocalDateTime dateCreation, Long utilisateurId) {
}
