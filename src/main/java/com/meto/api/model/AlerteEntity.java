package com.meto.api.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "alerte")
@Data
public class AlerteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String ville;

    @Column(name = "seuil_temperature")
    private Double seuilTemperature;

    @Column(name = "seuil_humidite")
    private Double seuilHumidite;

    @Column(name = "duree_activation_jours")
    private Integer dureeActivationJours;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @Column(name = "active", nullable = false)
    private boolean active = true;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;


    @PrePersist
    public void prePersist() {
        this.dateCreation = LocalDateTime.now();
    }
}

