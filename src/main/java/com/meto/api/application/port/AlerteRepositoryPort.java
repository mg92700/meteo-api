package com.meto.api.application.port;

import com.meto.api.domain.model.Alerte;

import java.util.List;

public interface AlerteRepositoryPort {

    List<Alerte> findByVilleAndActiveTrue(String ville);

}
