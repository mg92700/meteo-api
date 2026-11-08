package com.meto.api.infrastructure.adapter.messaging;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;
import com.meto.api.domain.model.Alerte;
import com.meto.api.domain.model.Meteo;
import com.meto.api.application.service.AlerteApplicationService;
import com.meto.api.domain.service.AlerteDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

/** Alertes à partir des relevés publiés par meteo-scheduling ; actif seulement avec meteo.kafka.enabled=true. */
@Component
@ConditionalOnProperty(name = "meteo.kafka.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class WeatherKafkaConsumer {

    private final AlerteApplicationService alerteApplicationService;
    private final AlerteDomainService alerteDomainService;
    private static final ObjectMapper OBJECT_MAPPER = JsonMapper.builder().build();


    @KafkaListener(topics = "meteo-topic", groupId = "meteo-group")
    public void consume(String message) {
        try {
            Meteo meteoData = OBJECT_MAPPER.readValue(message, Meteo.class);

            // 1. Récupère les alertes pour la ville
            List<Alerte> alertes = alerteApplicationService.getAlertesActivesParVille(meteoData.getCity());

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
