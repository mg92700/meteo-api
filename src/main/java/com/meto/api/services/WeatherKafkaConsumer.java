package com.meto.api.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.meto.api.pojo.Alerte;
import com.meto.api.pojo.Meteo;
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


    @KafkaListener(topics = "meteo-topic", groupId = "meteo-group")
    public void consume(String message) {
        try {
            Meteo meteoData = new ObjectMapper().readValue(message, Meteo.class);

            // 1. Récupère les alertes pour la ville
            List<Alerte> alertes = alerteService.getAlertesActivesParVille(meteoData.getCity());

            // 2. Filtrage des alertes selon les seuils
            for (Alerte alerte : alertes) {
                if (isAlerteDeclenchee(alerte, meteoData)) {
                    // 3. Envoie de la notification
                   // notificationPublisher.sendNotification(alerte.getUser(), alerte, meteoData);
                }
            }

        } catch (Exception e) {
            log.error("Erreur lors du traitement du message Kafka", e);
        }
    }

    private boolean isAlerteDeclenchee(Alerte alerte, Meteo meteo) {
        boolean seuilTemperatureDepasse = alerte.getSeuilTemperature() != null &&
                 Double.parseDouble(meteo.getProbafog()) >= alerte.getSeuilTemperature();

        boolean seuilHumiditeDepasse = alerte.getSeuilHumidite() != null &&
               Double.parseDouble( meteo.getTsoil1()) >= alerte.getSeuilHumidite();

        return seuilTemperatureDepasse || seuilHumiditeDepasse;
    }

}
