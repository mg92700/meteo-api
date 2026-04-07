package com.meto.api.infrastructure.adapter.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meto.api.domain.model.Alerte;
import com.meto.api.domain.model.Meteo;
import com.meto.api.application.service.AlerteService;
import com.meto.api.domain.service.AlerteDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jmx.export.notification.NotificationPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WeatherKafkaConsumer {

    private final AlerteService alerteService;
    private final AlerteDomainService alerteDomainService;


    @KafkaListener(topics = "meteo-topic", groupId = "meteo-group")
    public void consume(String message) {
        try {
            Meteo meteoData = new ObjectMapper().readValue(message, Meteo.class);

            // 1. Récupère les alertes pour la ville
            List<Alerte> alertes = alerteService.getAlertesActivesParVille(meteoData.getCity());

            // 2. Filtrage des alertes selon les seuils
            for (Alerte alerte : alertes) {
                if (alerteDomainService.isAlerteDeclenchee(alerte, meteoData)) {
                    // 3. Envoie de la notification
                   // notificationPublisher.sendNotification(alerte.getUser(), alerte, meteoData);
                }
            }

        } catch (Exception e) {
            log.error("Erreur lors du traitement du message Kafka", e);
        }
    }

}
