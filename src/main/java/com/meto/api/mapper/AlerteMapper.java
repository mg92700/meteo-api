package com.meto.api.mapper;

import com.meto.api.dto.AlerteDto;
import com.meto.api.model.AlerteEntity;
import com.meto.api.pojo.Alerte;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlerteMapper {

    AlerteDto toDto(Alerte alerte);
    Alerte toEntity(AlerteDto dto);
    Alerte toAlerte(AlerteEntity alerteEntity);
    List<Alerte> toAlertes(List<AlerteEntity> alerteEntities);
}
