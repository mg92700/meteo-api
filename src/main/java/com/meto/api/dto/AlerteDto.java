package com.meto.api.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlerteDto {

    private String ville;
    private Double seuilTemperature;
    private Double seuilHumidite;
    private int dureeActivationJours;
    private boolean active;
    private LocalDateTime dateCreation;
    private Long utilisateurId;
}
