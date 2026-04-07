package com.meto.api.infrastructure.adapter.persistence;

import com.meto.api.application.mapper.AlerteMapper;
import com.meto.api.application.port.AlerteRepositoryPort;
import com.meto.api.domain.model.Alerte;
import com.meto.api.infrastructure.adapter.persistence.entity.AlerteEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AlerteRepositoryAdapter implements AlerteRepositoryPort {

    private final AlerteJpaRepository jpaRepository;
    private final AlerteMapper mapper;

    @Override
    public List<Alerte> findByVilleAndActiveTrue(String ville) {
        List<AlerteEntity> entities = jpaRepository.findByVilleAndActiveTrue(ville);
        return entities.stream()
                .map(mapper::toAlerte)
                .collect(Collectors.toList());
    }
}
