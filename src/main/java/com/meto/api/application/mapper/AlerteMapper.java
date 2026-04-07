package com.meto.api.application.mapper;

import com.meto.api.application.dto.AlerteDto;
import com.meto.api.infrastructure.adapter.persistence.entity.AlerteEntity;
import com.meto.api.domain.model.Alerte;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlerteMapper {

    AlerteDto toDto(Alerte alerte);
    Alerte toEntity(AlerteDto dto);
    Alerte toAlerte(AlerteEntity alerteEntity);
    List<Alerte> toAlertes(List<AlerteEntity> alerteEntities);
}
