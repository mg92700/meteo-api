package com.meto.api.domain.service;

import com.meto.api.domain.model.Alerte;
import com.meto.api.domain.model.Meteo;
import org.springframework.stereotype.Component;

@Component
public class AlerteDomainService {

    public boolean isAlerteDeclenchee(Alerte alerte, Meteo meteo) {
        boolean seuilTemperatureDepasse = alerte.getSeuilTemperature() != null &&
                 Double.parseDouble(meteo.getProbafog()) >= alerte.getSeuilTemperature();

        boolean seuilHumiditeDepasse = alerte.getSeuilHumidite() != null &&
               Double.parseDouble(meteo.getTsoil1()) >= alerte.getSeuilHumidite();

        return seuilTemperatureDepasse || seuilHumiditeDepasse;
    }
}
